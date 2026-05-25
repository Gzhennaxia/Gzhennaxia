import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import InvestmentLayout from './InvestmentLayout';
import InvestmentOverview from './InvestmentOverview';
import PositionList from './PositionList';
import InvestmentTradeList from './InvestmentTradeList';

const InvestmentRoutes: React.FC = () => (
  <Routes>
    <Route path="/" element={<InvestmentLayout />}>
      <Route index element={<Navigate to="dashboard" replace />} />
      <Route path="dashboard" element={<InvestmentOverview />} />
      <Route path="positions" element={<PositionList />} />
      <Route path="trades" element={<InvestmentTradeList />} />
    </Route>
  </Routes>
);

export default InvestmentRoutes;
