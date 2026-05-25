/** 流水类型（与后端 AccountingConstants 一致） */
export const TX_TYPE_EXPENSE = 'expense';
export const TX_TYPE_INCOME = 'income';
export const TX_TYPE_TRANSFER = 'transfer';

/** 流水类型 → 中文展示 */
export const TX_TYPE_LABELS: Record<string, string> = {
  [TX_TYPE_EXPENSE]: '支出',
  [TX_TYPE_INCOME]: '收入',
  [TX_TYPE_TRANSFER]: '转账',
};

/**
 * 流水类型转中文标签。
 */
export function formatTxType(type?: string): string {
  if (!type) {
    return '-';
  }
  return TX_TYPE_LABELS[type] ?? type;
}

/** 资金账户类型选项（表单） */
export const ACCOUNT_TYPE_OPTIONS = [
  { label: '银行卡', value: 'bank' },
  { label: '支付宝', value: 'alipay' },
  { label: '微信', value: 'wechat' },
  { label: '现金', value: 'cash' },
  { label: '信用卡', value: 'credit' },
  { label: '虚拟账户', value: 'virtual' },
  { label: '其他', value: 'other' },
] as const;

/** 账户类型 → 中文展示 */
export const ACCOUNT_TYPE_LABELS: Record<string, string> = Object.fromEntries(
  ACCOUNT_TYPE_OPTIONS.map((o) => [o.value, o.label]),
);

/**
 * 账户类型转中文标签。
 */
export function formatAccountType(type?: string): string {
  if (!type) {
    return '-';
  }
  return ACCOUNT_TYPE_LABELS[type] ?? type;
}

/**
 * 账户编号脱敏：仅保留后四位，前面用 * 代替。
 */
export function maskAccountNo(accountNo?: string): string {
  if (!accountNo) {
    return '-';
  }
  const s = accountNo.trim();
  if (s.length <= 4) {
    return '*'.repeat(s.length);
  }
  const maskLen = Math.min(s.length - 4, 12);
  return '*'.repeat(maskLen) + s.slice(-4);
}
