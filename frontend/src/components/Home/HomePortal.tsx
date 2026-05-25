import type { CSSProperties, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  CheckSquareOutlined,
  CalendarOutlined,
  AccountBookOutlined,
  FundOutlined,
  LineChartOutlined,
  BookOutlined,
  SettingOutlined,
  RightOutlined,
} from '@ant-design/icons';
import './HomePortal.css';

/** 子系统入口配置 */
interface SubsystemEntry {
  /** 路由路径 */
  path: string;
  /** 展示名称 */
  title: string;
  /** 简短说明 */
  description: string;
  /** 图标组件 */
  icon: ReactNode;
  /** 卡片主题色 */
  accent: string;
}

const SUBSYSTEMS: SubsystemEntry[] = [
  {
    path: '/tasks',
    title: '任务管理',
    description: '日常任务、待办清单与收集箱',
    icon: <CheckSquareOutlined />,
    accent: '#4a90e2',
  },
  {
    path: '/calendar',
    title: '日历视图',
    description: '按日/周/月查看任务安排',
    icon: <CalendarOutlined />,
    accent: '#52c41a',
  },
  {
    path: '/accounting/dashboard',
    title: '个人记账',
    description: '收支流水、资金账户、分类与月报',
    icon: <AccountBookOutlined />,
    accent: '#722ed1',
  },
  {
    path: '/investment/dashboard',
    title: '投资管理',
    description: '持仓市值、买卖流水与资产配置',
    icon: <FundOutlined />,
    accent: '#eb2f96',
  },
  {
    path: '/finance/dashboard',
    title: '金融监控',
    description: '行情监控、投资组合跟踪',
    icon: <LineChartOutlined />,
    accent: '#fa8c16',
  },
  {
    path: '/question-bank',
    title: '题库系统',
    description: 'PDF 解析、题目提取与管理',
    icon: <BookOutlined />,
    accent: '#13c2c2',
  },
  {
    path: '/admin/dashboard',
    title: '管理后台',
    description: '系统字典与基础数据维护',
    icon: <SettingOutlined />,
    accent: '#595959',
  },
];

/**
 * 综合入口首页，导航至各业务子系统。
 */
const HomePortal: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="home-portal">
      <header className="home-portal__header">
        <div className="home-portal__brand">
          <span className="home-portal__logo">G</span>
          <div>
            <h1 className="home-portal__title">Gzhennaxia</h1>
            <p className="home-portal__subtitle">个人人生管理系统</p>
          </div>
        </div>
      </header>

      <main className="home-portal__main">
        <p className="home-portal__hint">选择要进入的子系统</p>
        <div className="home-portal__grid">
          {SUBSYSTEMS.map((item) => (
            <button
              key={item.path}
              type="button"
              className="home-portal__card"
              style={{ '--card-accent': item.accent } as CSSProperties}
              onClick={() => navigate(item.path)}
            >
              <span className="home-portal__card-icon">{item.icon}</span>
              <span className="home-portal__card-body">
                <span className="home-portal__card-title">{item.title}</span>
                <span className="home-portal__card-desc">{item.description}</span>
              </span>
              <RightOutlined className="home-portal__card-arrow" />
            </button>
          ))}
        </div>
      </main>

      <footer className="home-portal__footer">
        <span>任务 · 记账 · 投资 · 金融 · 题库 · 管理</span>
      </footer>
    </div>
  );
};

export default HomePortal;
