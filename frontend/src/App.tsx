import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import HomePortal from './components/Home/HomePortal';
import Layout from './components/Layout/Layout';
import TaskList from './components/TaskList/TaskList';
import CalendarView from './components/Calendar/CalendarView';
import MobileApp from './components/Mobile/MobileApp';
import AdminRoutes from './components/Admin/AdminRoutes';
import { QuestionBankLayout } from './components/QuestionBank';
import FinanceRoutes from './components/Finance/FinanceRoutes';
import AccountingRoutes from './components/Accounting/AccountingRoutes';
import InvestmentRoutes from './components/Investment/InvestmentRoutes';
import './App.css';

const App: React.FC = () => {
  const [isMobile, setIsMobile] = useState<boolean>(false);

  useEffect(() => {
    const checkDevice = () => {
      const userAgent = navigator.userAgent.toLowerCase();
      const isMobileDevice = /android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i.test(userAgent);
      const isSmallScreen = window.innerWidth <= 768;
      setIsMobile(isMobileDevice || isSmallScreen);
    };

    checkDevice();
    window.addEventListener('resize', checkDevice);
    return () => window.removeEventListener('resize', checkDevice);
  }, []);

  // 如果是移动端，显示专门的移动端应用
  if (isMobile) {
    return (
      <ConfigProvider locale={zhCN}>
        <MobileApp />
      </ConfigProvider>
    );
  }

  // PC端应用
  return (
    <ConfigProvider locale={zhCN}>
      <Router>
        <Routes>
          {/* 管理后台路由 - 独立布局 */}
          <Route path="/admin/*" element={<AdminRoutes />} />
          
          {/* 题库模块路由 - 独立布局 */}
          <Route path="/question-bank/*" element={<QuestionBankLayout />} />
          
          {/* 个人财务系统路由 - 独立布局 */}
          <Route path="/finance/*" element={<FinanceRoutes />} />

          {/* 个人记账 - 消费收支 */}
          <Route path="/accounting/*" element={<AccountingRoutes />} />

          {/* 投资管理 - 持仓与交易 */}
          <Route path="/investment/*" element={<InvestmentRoutes />} />

          {/* 综合入口首页 */}
          <Route path="/" element={<HomePortal />} />

          {/* 任务模块 - 使用全局 Layout */}
          <Route path="/tasks" element={<Layout><TaskList /></Layout>} />
          <Route path="/calendar" element={<Layout><CalendarView /></Layout>} />
        </Routes>
      </Router>
    </ConfigProvider>
  );
};

export default App;