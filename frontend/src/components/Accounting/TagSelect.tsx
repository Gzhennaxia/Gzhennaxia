import React, { useEffect, useState } from 'react';
import { Select, Divider, Button, message, Tag } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { accountingService } from '../../services/accountingService';
import type { AccTag } from '../../types/Accounting';
import { toAntTagColor } from '../../constants/tagColors';

interface TagSelectProps {
  value?: number[];
  onChange?: (value: number[]) => void;
  /** 父组件预加载的标签列表，避免编辑时仅显示 ID */
  options?: AccTag[];
}

/**
 * 标签多选：可选已有标签，也可输入新名称并创建。
 */
const TagSelect: React.FC<TagSelectProps> = ({ value, onChange, options: optionsProp }) => {
  const [tags, setTags] = useState<AccTag[]>(optionsProp ?? []);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);

  const loadTags = () => accountingService.listTags().then(setTags);

  useEffect(() => {
    if (optionsProp) {
      setTags(optionsProp);
      return;
    }
    loadTags();
  }, [optionsProp]);

  const trimmedSearch = search.trim();
  const canCreate =
    trimmedSearch.length > 0 && !tags.some((t) => t.name === trimmedSearch);

  const handleCreate = async () => {
    if (!canCreate) {
      return;
    }
    setLoading(true);
    try {
      const id = await accountingService.quickSaveTag({ name: trimmedSearch });
      await loadTags();
      const next = [...(value ?? [])];
      if (!next.includes(id)) {
        next.push(id);
      }
      onChange?.(next);
      setSearch('');
      message.success('标签已添加');
    } catch (e: unknown) {
      const err = e as { message?: string };
      message.error(err.message || '创建失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Select
      mode="multiple"
      allowClear
      showSearch
      placeholder="选择或搜索标签"
      value={value}
      onChange={onChange}
      onSearch={setSearch}
      filterOption={(input, option) =>
        (option?.label as string)?.toLowerCase().includes(input.toLowerCase())
      }
      options={tags.map((t) => ({
        label: t.name,
        value: Number(t.id),
        color: t.color,
      }))}
      optionFilterProp="label"
      optionRender={(option) => (
        <Tag color={toAntTagColor(option.data?.color as string)} style={{ margin: 0 }}>
          {option.label}
        </Tag>
      )}
      tagRender={({ label, value, closable, onClose }) => {
        const tag = tags.find((t) => Number(t.id) === value);
        return (
          <Tag
            color={toAntTagColor(tag?.color)}
            closable={closable}
            onClose={onClose}
            style={{ marginInlineEnd: 4 }}
          >
            {label}
          </Tag>
        );
      }}
      dropdownRender={(menu) => (
        <>
          {menu}
          {canCreate && (
            <>
              <Divider style={{ margin: '8px 0' }} />
              <div style={{ padding: '4px 8px 8px' }}>
                <Button
                  type="link"
                  icon={<PlusOutlined />}
                  loading={loading}
                  onClick={handleCreate}
                  style={{ padding: 0 }}
                >
                  新建标签「{trimmedSearch}」
                </Button>
              </div>
            </>
          )}
        </>
      )}
    />
  );
};

export default TagSelect;
