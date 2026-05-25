import React, { useState } from 'react';
import { Button, Space, Typography } from 'antd';
import { EyeOutlined, EyeInvisibleOutlined } from '@ant-design/icons';
import { maskAccountNo } from '../../constants/accounting';

interface MaskedAccountNoProps {
  accountNo?: string;
}

/**
 * 账户编号脱敏展示，点击眼睛切换完整/脱敏。
 */
const MaskedAccountNo: React.FC<MaskedAccountNoProps> = ({ accountNo }) => {
  const [revealed, setRevealed] = useState(false);

  if (!accountNo?.trim()) {
    return <Typography.Text type="secondary">-</Typography.Text>;
  }

  const text = revealed ? accountNo.trim() : maskAccountNo(accountNo);

  return (
    <Space size={4}>
      <Typography.Text copyable={revealed ? { text: accountNo.trim() } : false}>
        {text}
      </Typography.Text>
      <Button
        type="text"
        size="small"
        aria-label={revealed ? '隐藏编号' : '显示完整编号'}
        icon={revealed ? <EyeInvisibleOutlined /> : <EyeOutlined />}
        onClick={() => setRevealed((v) => !v)}
      />
    </Space>
  );
};

export default MaskedAccountNo;
