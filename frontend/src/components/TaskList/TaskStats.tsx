import React from 'react';
import { Card, Row, Col, Statistic } from 'antd';
import { 
  CheckCircleOutlined, 
  ClockCircleOutlined, 
 
} from '@ant-design/icons';
import { Task } from '../../types/Task';

interface TaskStatsProps {
  tasks: Task[];
}

const TaskStats: React.FC<TaskStatsProps> = ({ tasks }) => {
  const stats = {
    total: tasks.length,
    completed: tasks.filter(task => task.status === 'COMPLETED').length,
    inProgress: tasks.filter(task => task.status === 'IN_PROGRESS').length,
    pending: tasks.filter(task => task.status === 'PENDING').length,
    cancelled: tasks.filter(task => task.status === 'CANCELLED').length,
  };

  const completionRate = stats.total > 0 ? Math.round((stats.completed / stats.total) * 100) : 0;

  return (
    <Row gutter={[16, 16]} style={{ marginBottom: 32 }}>
      <Col xs={12} sm={6}>
        <Card className="stats-card" style={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          border: 'none',
          color: 'white'
        }}>
          <Statistic
            title="📋 总任务"
            value={stats.total}
            valueStyle={{ color: 'white', fontWeight: '700', fontSize: '28px' }}
          />
        </Card>
      </Col>
      <Col xs={12} sm={6}>
        <Card className="stats-card" style={{
          background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
          border: 'none',
          color: 'white'
        }}>
          <Statistic
            title="✅ 已完成"
            value={stats.completed}
            valueStyle={{ color: 'white', fontWeight: '700', fontSize: '28px' }}
            prefix={<CheckCircleOutlined style={{ color: 'rgba(255,255,255,0.8)' }} />}
          />
        </Card>
      </Col>
      <Col xs={12} sm={6}>
        <Card className="stats-card" style={{
          background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
          border: 'none',
          color: 'white'
        }}>
          <Statistic
            title="⏳ 进行中"
            value={stats.inProgress}
            valueStyle={{ color: 'white', fontWeight: '700', fontSize: '28px' }}
            prefix={<ClockCircleOutlined style={{ color: 'rgba(255,255,255,0.8)' }} />}
          />
        </Card>
      </Col>
      <Col xs={12} sm={6}>
        <Card className="stats-card" style={{
          background: completionRate >= 70 
            ? 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
            : 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
          border: 'none',
          color: 'white'
        }}>
          <Statistic
            title="🏆 完成率"
            value={completionRate}
            suffix="%"
            valueStyle={{ color: 'white', fontWeight: '700', fontSize: '28px' }}
          />
        </Card>
      </Col>
    </Row>
  );
};

export default TaskStats;