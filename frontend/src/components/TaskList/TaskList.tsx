import React, { useState, useEffect } from 'react';
import { List, Input, Select, Button, DatePicker, Popover, message, Tabs } from 'antd';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { Task } from '../../types/Task';
import { taskService } from '../../services/taskService';
import LoadingSpinner from '../Common/LoadingSpinner';
import TickTickTaskCard from './TickTickTaskCard';

import dayjs from 'dayjs';

const { Option } = Select;

const TaskList: React.FC = () => {

  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [priorityFilter, setPriorityFilter] = useState<string>('');
  const [quickTaskTitle, setQuickTaskTitle] = useState<string>('');
  const [quickTaskDate, setQuickTaskDate] = useState<string>(new Date().toISOString().split('T')[0]);
  const [activeTab, setActiveTab] = useState<string>('all');



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

      // 状态字典转换：字符串 -> 数字
      const currentStatus = typeof task.status === 'string' ?
        (task.status === 'COMPLETED' ? 2 : task.status === 'IN_PROGRESS' ? 1 : 0) :
        task.status;

      const newStatusNumber = currentStatus === 2 ? 0 : 2; // 0: PENDING, 2: COMPLETED

      // 使用正确的 TaskFormData 格式，包含状态字段
      const taskFormData = {
        title: task.title,
        description: task.description || '',
        startTime: task.startTime || new Date().toISOString().slice(0, 19),
        endTime: task.endTime || new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().slice(0, 19),
        priority: task.priority === 'HIGH' ? 3 : task.priority === 'MEDIUM' ? 2 : 1,
        category: task.category || '日常',
        tags: Array.isArray(task.tags) ? task.tags : (task.tags ? [task.tags] : []),
        status: newStatusNumber  // 使用数字状态：0=PENDING, 1=IN_PROGRESS, 2=COMPLETED
      };

      await taskService.updateTask(taskId, taskFormData);
      message.success('任务状态更新成功');
      loadTasks();
    } catch (error) {
      console.error('Failed to toggle task status:', error);
      message.error('更新任务状态失败');
    }
  };

  const handleQuickAdd = async () => {
    if (!quickTaskTitle.trim()) {
      message.warning('请输入任务标题');
      return;
    }

    try {
      const today = new Date();
      const taskFormData = {
        title: quickTaskTitle.trim(),
        description: '',
        startTime: today.toISOString().slice(0, 19),
        endTime: new Date(today.getTime() + 24 * 60 * 60 * 1000).toISOString().slice(0, 19),
        priority: 2, // 默认中等优先级
        category: '日常',
        tags: []
      };

      await taskService.createTask(taskFormData);
      message.success('任务创建成功');
      setQuickTaskTitle('');
      setQuickTaskDate(new Date().toISOString().split('T')[0]);
      loadTasks();
    } catch (error) {
      console.error('Failed to create task:', error);
      message.error('创建任务失败');
    }
  };

  const filteredTasks = tasks.filter(task => {
    const matchesSearch = task.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         (task.description?.toLowerCase().includes(searchTerm.toLowerCase()) ?? false);

    return matchesSearch;
  });

  const getTasksByStatus = (status: string) => {
    switch (status) {
      case 'pending':
        return filteredTasks.filter(task => task.status === 'PENDING' || task.status === 0);
      case 'completed':
        return filteredTasks.filter(task => task.status === 'COMPLETED' || task.status === 2);
      case 'all':
      default:
        return filteredTasks;
    }
  };

  const getTaskCount = (status: string) => {
    return getTasksByStatus(status).length;
  };

  const quickAddContent = (
    <div style={{ width: 300, padding: '8px 0' }}>
      <Input
        placeholder="输入任务标题..."
        value={quickTaskTitle}
        onChange={(e) => setQuickTaskTitle(e.target.value)}
        onPressEnter={handleQuickAdd}
        style={{ marginBottom: 8 }}
      />
      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        <DatePicker
          value={quickTaskDate ? dayjs(quickTaskDate) : null}
          onChange={(date) => setQuickTaskDate(date ? date.format('YYYY-MM-DD') : '')}
          placeholder="选择日期"
          style={{ flex: 1 }}
        />
        <Button type="primary" onClick={handleQuickAdd} size="small">
          添加
        </Button>
      </div>
    </div>
  );



  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ 
        display: 'flex', 
        justifyContent: 'flex-end', 
        alignItems: 'center', 
        marginBottom: '20px',
        flexWrap: 'wrap',
        gap: '12px'
      }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center', flexWrap: 'wrap' }}>
          <Input
            placeholder="搜索任务..."
            prefix={<SearchOutlined />}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ width: 200 }}
            allowClear
          />

          <Popover
            content={quickAddContent}
            title="快速添加任务"
            trigger="click"
            placement="bottomRight"
          >
            <Button type="primary" icon={<PlusOutlined />}>
              快速添加
            </Button>
          </Popover>
        </div>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={[
            {
              key: 'all',
              label: `全部 (${getTaskCount('all')})`,
              children: (
                <List
                  grid={{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 3, xxl: 4 }}
                  dataSource={getTasksByStatus('all')}
                  renderItem={(task) => (
                    <List.Item>
                      <TickTickTaskCard
                        key={task.id}
                        task={task}
                        onEdit={handleEdit}
                        onDelete={handleDelete}
                        onToggleComplete={handleToggleComplete}
                      />
                    </List.Item>
                  )}
                  locale={{ emptyText: '暂无任务' }}
                />
              ),
            },
            {
              key: 'pending',
              label: `待办 (${getTaskCount('pending')})`,
              children: (
                <List
                  grid={{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 3, xxl: 4 }}
                  dataSource={getTasksByStatus('pending')}
                  renderItem={(task) => (
                    <List.Item>
                      <TickTickTaskCard
                        key={task.id}
                        task={task}
                        onEdit={handleEdit}
                        onDelete={handleDelete}
                        onToggleComplete={handleToggleComplete}
                      />
                    </List.Item>
                  )}
                  locale={{ emptyText: '暂无待办任务' }}
                />
              ),
            },
            {
              key: 'completed',
              label: `已完成 (${getTaskCount('completed')})`,
              children: (
                <List
                  grid={{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 3, xxl: 4 }}
                  dataSource={getTasksByStatus('completed')}
                  renderItem={(task) => (
                    <List.Item>
                      <TickTickTaskCard
                        key={task.id}
                        task={task}
                        onEdit={handleEdit}
                        onDelete={handleDelete}
                        onToggleComplete={handleToggleComplete}
                      />
                    </List.Item>
                  )}
                  locale={{ emptyText: '暂无已完成任务' }}
                />
              ),
            },
          ]}
        />
      )}
    </div>
  );
};

export default TaskList;