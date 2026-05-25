import React from 'react';
import { Space, Tooltip } from 'antd';
import { TAG_COLOR_PRESETS, type TagColorKey } from '../../constants/tagColors';

interface TagColorPickerProps {
  value?: string;
  onChange?: (value: TagColorKey) => void;
}

/**
 * 标签颜色选择：赤橙黄绿青蓝紫预设。
 */
const TagColorPicker: React.FC<TagColorPickerProps> = ({ value, onChange }) => (
  <Space wrap size={8}>
    {TAG_COLOR_PRESETS.map((preset) => {
      const selected = value === preset.key;
      return (
        <Tooltip key={preset.key} title={preset.label}>
          <button
            type="button"
            aria-label={preset.label}
            onClick={() => onChange?.(preset.key)}
            style={{
              width: 28,
              height: 28,
              borderRadius: 6,
              border: selected ? '2px solid #1677ff' : '2px solid transparent',
              background: preset.hex,
              cursor: 'pointer',
              padding: 0,
              boxShadow: selected ? '0 0 0 2px rgba(22,119,255,0.2)' : undefined,
            }}
          />
        </Tooltip>
      );
    })}
  </Space>
);

export default TagColorPicker;
