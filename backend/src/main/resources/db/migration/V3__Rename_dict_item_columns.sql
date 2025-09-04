-- 重命名dict_item表的字段
-- 1. type_code -> dict_code
ALTER TABLE dict_item RENAME COLUMN type_code TO dict_code;

-- 2. item_key -> item_code  
ALTER TABLE dict_item RENAME COLUMN item_key TO item_code;

-- 3. item_value -> item_name
ALTER TABLE dict_item RENAME COLUMN item_value TO item_name;

-- 4. 更新相关索引（如果存在的话）
-- 删除旧索引
DROP INDEX IF EXISTS idx_dict_item_type_code;
DROP INDEX IF EXISTS idx_dict_item_item_key;

-- 创建新索引
CREATE INDEX idx_dict_item_dict_code ON dict_item(dict_code);
CREATE INDEX idx_dict_item_item_code ON dict_item(item_code);
CREATE INDEX idx_dict_item_status ON dict_item(status);
CREATE INDEX idx_dict_item_sort ON dict_item(sort);