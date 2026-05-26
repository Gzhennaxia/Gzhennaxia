import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import FinanceLayout from './FinanceLayout';
import FinanceDashboard from './FinanceDashboard';
import MonitorList from './MonitorList';
import PortfolioList from './PortfolioList';
import Sp500IndexPage from './Sp500IndexPage';

const FinanceRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<FinanceLayout />}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<FinanceDashboard />} />
        <Route path="monitor" element={<MonitorList />} />
        <Route path="portfolio" element={<PortfolioList />} />
        <Route path="index/sp500" element={<Sp500IndexPage />} />
      </Route>
    </Routes>
  );
};

export default FinanceRoutes;