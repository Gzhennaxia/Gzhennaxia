import React, { useEffect, useState } from 'react';
import { Select, Divider, Button, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { accountingService } from '../../services/accountingService';
import type { AccChannel } from '../../types/Accounting';

interface ChannelSelectProps {
  value?: number;
  onChange?: (value: number | undefined) => void;
  /** 父组件预加载的渠道列表，避免编辑时仅显示 ID */
  options?: AccChannel[];
}

/**
 * 渠道单选：可选已有渠道，也可输入新名称并创建。
 */
const ChannelSelect: React.FC<ChannelSelectProps> = ({ value, onChange, options: optionsProp }) => {
  const [channels, setChannels] = useState<AccChannel[]>(optionsProp ?? []);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);

  const loadChannels = () => accountingService.listChannels().then(setChannels);

  useEffect(() => {
    if (optionsProp) {
      setChannels(optionsProp);
      return;
    }
    loadChannels();
  }, [optionsProp]);

  const trimmedSearch = search.trim();
  const canCreate =
    trimmedSearch.length > 0 && !channels.some((c) => c.name === trimmedSearch);

  const handleCreate = async () => {
    if (!canCreate) {
      return;
    }
    setLoading(true);
    try {
      const id = await accountingService.saveChannel({ name: trimmedSearch });
      await loadChannels();
      onChange?.(id);
      setSearch('');
      message.success('渠道已添加');
    } catch (e: unknown) {
      const err = e as { message?: string };
      message.error(err.message || '创建失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Select
      allowClear
      showSearch
      placeholder="选择或搜索渠道"
      value={value}
      onChange={onChange}
      onSearch={setSearch}
      filterOption={(input, option) =>
        (option?.label as string)?.toLowerCase().includes(input.toLowerCase())
      }
      options={channels.map((c) => ({ label: c.name, value: Number(c.id) }))}
      optionFilterProp="label"
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
                  新建渠道「{trimmedSearch}」
                </Button>
              </div>
            </>
          )}
        </>
      )}
    />
  );
};

export default ChannelSelect;
