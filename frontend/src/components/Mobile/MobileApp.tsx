import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import TickTickMobile from './TickTickMobile';
import './MobileApp.css';

const MobileApp: React.FC = () => {
  return (
    <div className="mobile-app">
      <ConfigProvider locale={zhCN}>
        <TickTickMobile />
      </ConfigProvider>
    </div>
  );
};

export default MobileApp;