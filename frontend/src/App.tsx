import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import Layout from './components/Layout/Layout';
import TaskList from './components/TaskList/TaskList';
import CalendarView from './components/Calendar/CalendarView';
import MobileApp from './components/Mobile/MobileApp';
import DictList from './components/Admin/DictManagement/DictList';
import DictItemList from './components/Admin/DictManagement/DictItemList';
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
        <Layout>
          <Routes>
            <Route path="/" element={<TaskList />} />
            <Route path="/calendar" element={<CalendarView />} />
            <Route path="/admin/dict" element={<DictList />} />
            <Route path="/admin/dict/:code" element={<DictItemList />} />
          </Routes>
        </Layout>
      </Router>
    </ConfigProvider>
  );
};

export default App;