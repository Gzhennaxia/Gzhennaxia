import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import AccountingLayout from './AccountingLayout';
import AccountingDashboard from './AccountingDashboard';
import TransactionList from './TransactionList';
import AccountList from './AccountList';
import CategoryManage from './CategoryManage';
import TagManage from './TagManage';
import MonthlyReport from './MonthlyReport';

const AccountingRoutes: React.FC = () => (
  <Routes>
    <Route path="/" element={<AccountingLayout />}>
      <Route index element={<Navigate to="dashboard" replace />} />
      <Route path="dashboard" element={<AccountingDashboard />} />
      <Route path="transactions" element={<TransactionList />} />
      <Route path="add" element={<Navigate to="/accounting/dashboard" replace />} />
      <Route path="accounts" element={<AccountList />} />
      <Route path="categories" element={<CategoryManage />} />
      <Route path="tags" element={<TagManage />} />
      <Route path="reports" element={<MonthlyReport />} />
      {/* 旧路径兼容，跳转至投资子系统 */}
      <Route path="investment" element={<Navigate to="/investment/dashboard" replace />} />
      <Route path="positions" element={<Navigate to="/investment/positions" replace />} />
      <Route path="investment-trades" element={<Navigate to="/investment/trades" replace />} />
    </Route>
  </Routes>
);

export default AccountingRoutes;
