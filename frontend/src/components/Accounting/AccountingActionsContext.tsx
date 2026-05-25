import { createContext, useContext } from 'react';
import type { TransactionFormType } from './TransactionFormModal';

interface AccountingActionsContextValue {
  /** 打开记一笔弹窗 */
  openAddTransaction: (defaultType?: TransactionFormType) => void;
}

export const AccountingActionsContext = createContext<AccountingActionsContextValue>({
  openAddTransaction: () => {},
});

export function useAccountingActions(): AccountingActionsContextValue {
  return useContext(AccountingActionsContext);
}
