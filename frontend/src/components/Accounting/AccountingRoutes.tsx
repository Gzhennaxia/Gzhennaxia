import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import AccountingLayout from './AccountingLayout';
import AccountingDashboard from './AccountingDashboard';
import TransactionList from './TransactionList';
import TransactionForm from './TransactionForm';
import AccountList from './AccountList';
import InvestmentOverview from './InvestmentOverview';
import PositionList from './PositionList';
import InvestmentTradeList from './InvestmentTradeList';
import MonthlyReport from './MonthlyReport';

const AccountingRoutes: React.FC = () => (
  <Routes>
    <Route path="/" element={<AccountingLayout />}>
      <Route index element={<Navigate to="dashboard" replace />} />
      <Route path="dashboard" element={<AccountingDashboard />} />
      <Route path="transactions" element={<TransactionList />} />
      <Route path="add" element={<TransactionForm />} />
      <Route path="accounts" element={<AccountList />} />
      <Route path="investment" element={<InvestmentOverview />} />
      <Route path="positions" element={<PositionList />} />
      <Route path="investment-trades" element={<InvestmentTradeList />} />
      <Route path="reports" element={<MonthlyReport />} />
    </Route>
  </Routes>
);

export default AccountingRoutes;
