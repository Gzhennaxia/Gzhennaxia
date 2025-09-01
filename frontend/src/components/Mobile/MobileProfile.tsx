import { Card, List, Avatar, Button } from 'antd';
import { UserOutlined, SettingOutlined, LogoutOutlined } from '@ant-design/icons';
import './MobileProfile.css';

const MobileProfile: React.FC = () => {
  const menuItems = [
    { icon: <SettingOutlined />, title: '设置', description: '个人偏好设置' },
    { icon: <LogoutOutlined />, title: '退出登录', description: '安全退出应用' },
  ];

  return (
    <div className="mobile-profile">
      <Card className="profile-header">
        <div className="user-info">
          <Avatar size={64} icon={<UserOutlined />} />
          <div className="user-details">
            <h3>用户名</h3>
            <p>user@example.com</p>
          </div>
        </div>
      </Card>

      <Card className="profile-menu">
        <List
          dataSource={menuItems}
          renderItem={(item) => (
            <List.Item>
              <List.Item.Meta
                avatar={item.icon}
                title={item.title}
                description={item.description}
              />
            </List.Item>
          )}
        />
      </Card>
    </div>
  );
};

export default MobileProfile;