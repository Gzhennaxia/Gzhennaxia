-- 添加字典表的删除标记字段
ALTER TABLE dict_type ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- 为现有数据设置默认值
UPDATE dict_type SET deleted = FALSE WHERE deleted IS NULL;

-- 添加索引以提高查询性能
CREATE INDEX idx_dict_type_deleted ON dict_type(deleted);
CREATE INDEX idx_dict_type_status_deleted ON dict_type(status, deleted);