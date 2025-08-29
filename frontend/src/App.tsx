import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import Layout from './components/Layout/Layout';
import TaskList from './components/TaskList/TaskList';
import CalendarView from './components/Calendar/CalendarView';
import './App.css';

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN}>
      <Router>
        <Layout>
          <Routes>
            <Route path="/" element={<TaskList />} />
            <Route path="/calendar" element={<CalendarView />} />
          </Routes>
        </Layout>
      </Router>
    </ConfigProvider>
  );
};

export default App;