import { Card, Button, Tag, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined, CalendarOutlined } from '@ant-design/icons';
import { InboxTask } from '../../types/InboxTask';
import dayjs from 'dayjs';

interface InboxTaskCardProps {
  task: InboxTask;
  onEdit: (task: InboxTask) => void;
  onDelete: (taskId: number) => void;
  onMoveToSchedule: (task: InboxTask) => void;
}

const InboxTaskCard: React.FC<InboxTaskCardProps> = ({
  task,
  onEdit,
  onDelete,
  onMoveToSchedule
}) => {
  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'HIGH': return 'red';
      case 'MEDIUM': return 'orange';
      case 'LOW': return 'green';
      default: return 'default';
    }
  };

  const getPriorityText = (priority: string) => {
    switch (priority) {
      case 'HIGH': return '高';
      case 'MEDIUM': return '中';
      case 'LOW': return '低';
      default: return '中';
    }
  };

  return (
    <Card
      size="small"
      className="inbox-task-card"
      style={{ marginBottom: 8 }}
      actions={[
        <Button
          key="schedule"
          type="text"
          icon={<CalendarOutlined />}
          onClick={() => onMoveToSchedule(task)}
          title="安排时间"
        />,
        <Button
          key="edit"
          type="text"
          icon={<EditOutlined />}
          onClick={() => onEdit(task)}
          title="编辑"
        />,
        <Popconfirm
          key="delete"
          title="确定要删除这个任务吗？"
          onConfirm={() => task.id && onDelete(task.id)}
          okText="确定"
          cancelText="取消"
        >
          <Button
            type="text"
            icon={<DeleteOutlined />}
            danger
            title="删除"
          />
        </Popconfirm>
      ]}
    >
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 8 }}>
          <h4 style={{ margin: 0, fontSize: 14, fontWeight: 500 }}>{task.title}</h4>
          <Tag color={getPriorityColor(task.priority)}>
            {getPriorityText(task.priority)}
          </Tag>
        </div>
        
        {task.description && (
          <p style={{ margin: '8px 0', fontSize: 12, color: '#666', lineHeight: 1.4 }}>
            {task.description}
          </p>
        )}
        
        <div style={{ fontSize: 11, color: '#999', marginTop: 8 }}>
          创建于 {dayjs(task.createdAt).format('MM-DD HH:mm')}
        </div>
      </div>
    </Card>
  );
};

export default InboxTaskCard;