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
  tags?: string;
  note?: string;
}

export interface AccTransactionVO extends AccTransaction {
  accountName?: string;
  targetAccountName?: string;
  categoryName?: string;
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
