/** 标签颜色预设：赤橙黄绿青蓝紫（Ant Design Tag color） */
export const TAG_COLOR_PRESETS = [
  { key: 'red', label: '赤', hex: '#f5222d' },
  { key: 'orange', label: '橙', hex: '#fa8c16' },
  { key: 'gold', label: '黄', hex: '#faad14' },
  { key: 'green', label: '绿', hex: '#52c41a' },
  { key: 'cyan', label: '青', hex: '#13c2c2' },
  { key: 'blue', label: '蓝', hex: '#1677ff' },
  { key: 'purple', label: '紫', hex: '#722ed1' },
] as const;

export type TagColorKey = (typeof TAG_COLOR_PRESETS)[number]['key'];

export const DEFAULT_TAG_COLOR: TagColorKey = 'blue';

/** 将存储值转为 Ant Tag 可用的 color */
export function toAntTagColor(color?: string): TagColorKey | string {
  if (!color) {
    return DEFAULT_TAG_COLOR;
  }
  const preset = TAG_COLOR_PRESETS.find((p) => p.key === color);
  return preset ? preset.key : color;
}
