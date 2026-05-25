/** 资金账户 */
export interface FundAccount {
  id?: number;
  name: string;
  accountNo?: string;
  type: string;
  currency?: string;
  balance?: number;
  includeInNetWorth?: number;
  sortOrder?: number;
  remark?: string;
}

/** 流水标签 */
export interface AccTag {
  id?: number;
  name: string;
  color?: string;
  sortOrder?: number;
  /** 关联流水条数，列表按此降序 */
  transactionCount?: number;
}

/** 收支渠道 */
export interface AccChannel {
  id?: number;
  name: string;
  sortOrder?: number;
}

/** 收支分类 */
export interface AccCategory {
  id?: number;
  name: string;
  parentId?: number;
  type: 'expense' | 'income';
  icon?: string;
  budgetMonthly?: number;
}

/** 流水 */
export interface AccTransaction {
  id?: number;
  type: 'expense' | 'income' | 'transfer';
  amount: number;
  accountId: number;
  targetAccountId?: number;
  categoryId?: number;
  tradeTime?: string;
  payee?: string;
  /** 渠道主键，可不传 */
  channelId?: number;
  /** 标签主键 ID 列表 */
  tagIds?: number[];
  note?: string;
}

export interface AccTransactionVO extends AccTransaction {
  accountName?: string;
  targetAccountName?: string;
  categoryName?: string;
  /** 渠道名称（展示） */
  channelName?: string;
  /** 标签名称（展示） */
  tagNames?: string[];
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

export interface DashboardVO {
  netWorth: number;
  liquidAssets: number;
  investmentValue: number;
  monthIncome: number;
  monthExpense: number;
  savingsRate: number;
  recentTransactions: AccTransactionVO[];
}

export interface InvestmentSymbol {
  id?: number;
  symbol: string;
  name: string;
  market?: string;
  currency?: string;
  currentPrice?: number;
}

export interface InvestmentTrade {
  id?: number;
  symbolId: number;
  tradeType: string;
  quantity?: number;
  price?: number;
  amount: number;
  fee?: number;
  tradeTime?: string;
  accountId?: number;
  note?: string;
}

export interface PositionVO {
  symbolId: number;
  symbol: string;
  name: string;
  market?: string;
  quantity: number;
  avgCost?: number;
  currentPrice?: number;
  marketValue?: number;
  unrealizedPnl?: number;
  unrealizedPnlPercent?: number;
  weightPercent?: number;
}

/** 批量导入单行错误 */
export interface AccTransactionImportError {
  rowNumber: number;
  message: string;
}

/** 批量导入结果 */
export interface AccTransactionImportResult {
  totalRows: number;
  successCount: number;
  failCount: number;
  errors: AccTransactionImportError[];
}

export interface MonthlyReportVO {
  year: number;
  month: number;
  income: number;
  expense: number;
  balance: number;
  savingsRate: number;
  expenseByCategory: {
    categoryId: number;
    categoryName: string;
    amount: number;
    budgetMonthly?: number;
    overBudget?: boolean;
  }[];
}
