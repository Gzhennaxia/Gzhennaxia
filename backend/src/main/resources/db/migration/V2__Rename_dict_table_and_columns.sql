-- 重命名表和字段
-- 1. 重命名表名 dict_type -> dict
ALTER TABLE dict_type RENAME TO dict;

-- 2. 重命名字段 code -> dict_code
ALTER TABLE dict RENAME COLUMN code TO dict_code;

-- 3. 重命名字段 name -> dict_name  
ALTER TABLE dict RENAME COLUMN name TO dict_name;

-- 4. 更新索引（如果存在的话）
-- 删除旧索引
DROP INDEX IF EXISTS idx_dict_type_code;
DROP INDEX IF EXISTS idx_dict_type_name;

-- 创建新索引
CREATE UNIQUE INDEX idx_dict_dict_code ON dict(dict_code);
CREATE INDEX idx_dict_dict_name ON dict(dict_name);
CREATE INDEX idx_dict_status ON dict(status);
CREATE INDEX idx_dict_deleted ON dict(deleted);