import { useNavigate } from 'react-router-dom';
import { HomeOutlined } from '@ant-design/icons';
import { Button } from 'antd';

/**
 * 子系统内返回综合入口首页。
 */
const BackToHome: React.FC = () => {
  const navigate = useNavigate();
  return (
    <Button type="link" icon={<HomeOutlined />} onClick={() => navigate('/')} style={{ marginLeft: 8 }}>
      返回首页
    </Button>
  );
};

export default BackToHome;
