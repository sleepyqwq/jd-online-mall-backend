package com.tq.module.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tq.common.api.ErrorCode;
import com.tq.common.exception.BusinessException;
import com.tq.common.exception.NotFoundException;
import com.tq.module.category.dto.CategoryCreateRequest;
import com.tq.module.category.dto.CategoryTreeNode;
import com.tq.module.category.dto.CategoryUpdateRequest;
import com.tq.module.category.entity.Category;
import com.tq.module.category.mapper.CategoryMapper;
import com.tq.module.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 分类业务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryTreeNode> listCategoryTree() {
        // 统一按 sortOrder、id 升序，保证前后台展示顺序一致
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSortOrder)
                        .orderByAsc(Category::getId)
        );

        Map<Long, CategoryTreeNode> nodeMap = new LinkedHashMap<>();
        List<CategoryTreeNode> roots = new ArrayList<>();

        // 先构造所有节点
        for (Category category : categories) {
            CategoryTreeNode node = toNode(category);
            nodeMap.put(category.getId(), node);
        }

        // 再按 parentId 组装树
        for (Category category : categories) {
            CategoryTreeNode node = nodeMap.get(category.getId());
            Long parentId = category.getParentId();
            if (parentId == null || parentId == 0L) {
                // 一级分类
                roots.add(node);
            } else {
                CategoryTreeNode parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    // 容错：若父级不存在，则作为一级分类返回，避免接口直接报错
                    roots.add(node);
                }
            }
        }

        return roots;
    }

    private CategoryTreeNode toNode(Category category) {
        CategoryTreeNode node = new CategoryTreeNode();
        node.setId(String.valueOf(category.getId()));
        Long pid = category.getParentId() == null ? 0L : category.getParentId();
        node.setParentId(String.valueOf(pid));
        node.setName(category.getName());
        node.setSortOrder(category.getSortOrder());
        return node;
    }

    @Override
    @Transactional
    public void createCategory(CategoryCreateRequest request) {
        Long parentId = request.getParentId();
        if (parentId == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "parentId 不能为空");
        }

        // 校验两级分类约束
        validateTwoLevelConstraint(parentId);

        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(parentId);
        category.setSortOrder(request.getSortOrder());

        LocalDateTime now = LocalDateTime.now();
        category.setCreateTime(now);
        category.setUpdateTime(now);

        categoryMapper.insert(category);
    }

    @Override
    @Transactional
    public void updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryMapper.selectById(id);
        if (category == null || (category.getDeleted() != null && category.getDeleted() == 1)) {
            throw new NotFoundException("分类不存在");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName().trim());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        if (request.getParentId() != null && !request.getParentId().equals(category.getParentId())) {
            Long newParentId = request.getParentId();
            if (id.equals(newParentId)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "分类的父级不能为自身");
            }

            // 若当前分类存在子分类，则不允许修改父级以避免出现第三级
            if (hasChildren(id)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "存在子分类的分类暂不支持修改父级");
            }

            validateTwoLevelConstraint(newParentId);
            category.setParentId(newParentId);
        }

        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // 1. 先查询是否存在（保持原有的校验逻辑）
        Category category = categoryMapper.selectById(id);
        if (category == null || (category.getDeleted() != null && category.getDeleted() == 1)) {
            throw new NotFoundException("分类不存在");
        }

        // 2. 校验子分类（保持原有逻辑）
        if (hasChildren(id)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "存在子分类，禁止删除");
        }

        // 3. 【修复核心】使用 UpdateWrapper 强制更新 deleted 字段
        // 既然 deleteById 报错，updateById 不生效，我们就手动构造 Update 语句
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, id)
                .set(Category::getDeleted, 1) // 显式设置逻辑删除
                .set(Category::getUpdateTime, LocalDateTime.now()); // 显式设置更新时间

        // 第一个参数传 null，表示不使用实体自动映射，全靠 wrapper 控制 SET 子句
        categoryMapper.update(null, updateWrapper);
    }

    /**
     * 校验两级分类约束：
     * - parentId = 0 表示一级分类；
     * - parentId 为非 0 时，要求父级本身必须为一级分类（其 parentId 为 0），
     *   从而保证只存在两级结构。
     */
    private void validateTwoLevelConstraint(Long parentId) {
        if (parentId == null || parentId == 0L) {
            return; // 一级分类
        }

        Category parent = categoryMapper.selectById(parentId);
        if (parent == null || (parent.getDeleted() != null && parent.getDeleted() == 1)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "上级分类不存在");
        }

        Long grandParentId = parent.getParentId();
        if (grandParentId != null && grandParentId != 0L) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "仅支持两级分类结构");
        }
    }

    /**
     * 判断分类是否存在子分类。
     */
    private boolean hasChildren(Long id) {
        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, id)
        );
        return count != null && count > 0;
    }
}