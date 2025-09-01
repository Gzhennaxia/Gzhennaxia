import { useState, useEffect } from 'react';
import { List, Input, Tabs, Card, Button, message } from 'antd';
import { SearchOutlined, CheckCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { Task } from '../../types/Task';
import { taskService } from '../../services/taskService';
import MobileTaskCard from './MobileTaskCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import './MobileTaskList.css';

const MobileTaskList: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [activeTab, setActiveTab] = useState<string>('all');
  const [refreshing, setRefreshing] = useState<boolean>(false);

  useEffect(() => {
    loadTasks();
  }, []);

  const loadTasks = async () => {
    try {
      setLoading(true);
      const data = await taskService.getAllTasks();
      setTasks(data);
    } catch (error) {
      console.error('Failed to load tasks:', error);
      message.error('加载任务失败');
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    setRefreshing(true);
    await loadTasks();
    setRefreshing(false);
  };

  const handleEdit = (task: Task) => {
    console.log('Edit task:', task);
  };

  const handleDelete = async (taskId: number) => {
    try {
      await taskService.deleteTask(taskId);
      message.success('任务删除成功');
      loadTasks();
    } catch (error) {
      console.error('Failed to delete task:', error);
      message.error('删除任务失败');
    }
  };

  const handleToggleComplete = async (taskId: number) => {
    try {
      const task = tasks.find(t => t.id === taskId);
      if (!task) return;

      const currentStatus = typeof task.status === 'string' ? 
        (task.status === 'COMPLETED' ? 2 : task.status === 'IN_PROGRESS' ? 1 : 0) : 
        task.status;
      
      const newStatusNumber = currentStatus === 2 ? 0 : 2;
      
      const taskFormData = {
        title: task.title,
        description: task.description || '',
        startTime: task.startTime || new Date().toISOString().slice(0, 19),
        endTime: task.endTime || new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().slice(0, 19),
        priority: task.priority === 'HIGH' ? 3 : task.priority === 'MEDIUM' ? 2 : 1,
        category: task.category || '日常',
        tags: Array.isArray(task.tags) ? task.tags : (task.tags ? [task.tags] : []),
        status: newStatusNumber
      };

      await taskService.updateTask(taskId, taskFormData);
      message.success('任务状态更新成功');
      loadTasks();
    } catch (error) {
      console.error('Failed to toggle task status:', error);
      message.error('更新任务状态失败');
    }
  };

  const filteredTasks = tasks.filter((task: Task) => {
    const matchesSearch = task.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         (task.description && task.description.toLowerCase().includes(searchTerm.toLowerCase()));
    return matchesSearch;
  });

  const getTasksByStatus = (status: string): Task[] => {
    switch (status) {
      case 'pending':
        return filteredTasks.filter((task: Task) => task.status === 'PENDING' || task.status === 0);
      case 'completed':
        return filteredTasks.filter((task: Task) => task.status === 'COMPLETED' || task.status === 2);
      case 'all':
      default:
        return filteredTasks;
    }
  };

  const getTaskCount = (status: string): number => {
    return getTasksByStatus(status).length;
  };

  const renderTaskList = (taskList: Task[]) => (
    <List
      dataSource={taskList}
      renderItem={(task: Task) => (
        <List.Item className="mobile-task-item">
          <MobileTaskCard
            task={task}
            onEdit={handleEdit}
            onDelete={handleDelete}
            onToggleComplete={handleToggleComplete}
          />
        </List.Item>
      )}
      locale={{ emptyText: '暂无任务' }}
    />
  );

  return (
    <div className="mobile-task-list">
      {/* 搜索栏 */}
      <div className="mobile-search-bar">
        <Input
          placeholder="搜索任务..."
          prefix={<SearchOutlined />}
          value={searchTerm}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setSearchTerm(e.target.value)}
          allowClear
        />
      </div>

      {/* 统计卡片 */}
      <div className="mobile-stats">
        <Card className="stat-card">
          <div className="stat-content">
            <ClockCircleOutlined className="stat-icon pending" />
            <div className="stat-info">
              <div className="stat-number">{getTaskCount('pending')}</div>
              <div className="stat-label">待办</div>
            </div>
          </div>
        </Card>
        <Card className="stat-card">
          <div className="stat-content">
            <CheckCircleOutlined className="stat-icon completed" />
            <div className="stat-info">
              <div className="stat-number">{getTaskCount('completed')}</div>
              <div className="stat-label">已完成</div>
            </div>
          </div>
        </Card>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          className="mobile-tabs"
          items={[
            {
              key: 'all',
              label: `全部 (${getTaskCount('all')})`,
              children: renderTaskList(getTasksByStatus('all')),
            },
            {
              key: 'pending',
              label: `待办 (${getTaskCount('pending')})`,
              children: renderTaskList(getTasksByStatus('pending')),
            },
            {
              key: 'completed',
              label: `已完成 (${getTaskCount('completed')})`,
              children: renderTaskList(getTasksByStatus('completed')),
            },
          ]}
        />
      )}
    </div>
  );
};

export default MobileTaskList;