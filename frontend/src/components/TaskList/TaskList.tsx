import React, { useState, useEffect } from 'react';
import { Card, List, Tag, Button, Space, Popconfirm, message, Select, Input } from 'antd';
import { EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { Task } from '../../types/Task';
import { taskService } from '../../services/taskService';
import TaskFormModal from '../TaskForm/TaskFormModal';
import TaskStats from './TaskStats';

const { Meta } = Card;
const { Search } = Input;

const TaskList: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [filteredTasks, setFilteredTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | undefined>();
  const [modalVisible, setModalVisible] = useState(false);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [priorityFilter, setPriorityFilter] = useState<string>('');

  useEffect(() => {
    loadTasks();
  }, []);

  useEffect(() => {
    filterTasks();
  }, [tasks, statusFilter, priorityFilter]);

  const loadTasks = async () => {
    setLoading(true);
    try {
      const data = await taskService.getAllTasks();
      setTasks(data);
    } catch (error) {
      message.error('加载任务失败');
      console.error('Load tasks failed:', error);
    } finally {
      setLoading(false);
    }
  };

  const filterTasks = () => {
    let filtered = [...tasks];
    
    if (statusFilter) {
      filtered = filtered.filter(task => task.status === statusFilter);
    }
    
    if (priorityFilter) {
      filtered = filtered.filter(task => task.priority === priorityFilter);
    }
    
    setFilteredTasks(filtered);
  };

  const handleSearch = (value: string) => {
    if (!value) {
      filterTasks();
      return;
    }
    
    const searchResults = filteredTasks.filter(task =>
      task.title.toLowerCase().includes(value.toLowerCase()) ||
      task.description?.toLowerCase().includes(value.toLowerCase()) ||
      task.category?.toLowerCase().includes(value.toLowerCase())
    );
    setFilteredTasks(searchResults);
  };

  const handleDelete = async (id: number) => {
    try {
      await taskService.deleteTask(id);
      message.success('任务删除成功');
      loadTasks();
    } catch (error) {
      message.error('删除任务失败');
      console.error('Delete task failed:', error);
    }
  };

  const handleEdit = (task: Task) => {
    setEditingTask(task);
    setModalVisible(true);
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'HIGH': return 'red';
      case 'MEDIUM': return 'orange';
      case 'LOW': return 'green';
      default: return 'default';
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED': return 'green';
      case 'IN_PROGRESS': return 'blue';
      case 'PENDING': return 'orange';
      case 'CANCELLED': return 'red';
      default: return 'default';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'COMPLETED': return '已完成';
      case 'IN_PROGRESS': return '进行中';
      case 'PENDING': return '待处理';
      case 'CANCELLED': return '已取消';
      default: return status;
    }
  };

  const getPriorityText = (priority: string) => {
    switch (priority) {
      case 'HIGH': return '高';
      case 'MEDIUM': return '中';
      case 'LOW': return '低';
      default: return priority;
    }
  };

  return (
    <div>
      <TaskStats tasks={tasks} />
      
      <div style={{ marginBottom: 16, display: 'flex', gap: 16, alignItems: 'center' }}>
        <Search
          placeholder="搜索任务..."
          allowClear
          onSearch={handleSearch}
          style={{ width: 300 }}
          prefix={<SearchOutlined />}
        />
        
        <Select
          placeholder="筛选状态"
          allowClear
          style={{ width: 120 }}
          onChange={setStatusFilter}
        >
          <Select.Option value="PENDING">待处理</Select.Option>
          <Select.Option value="IN_PROGRESS">进行中</Select.Option>
          <Select.Option value="COMPLETED">已完成</Select.Option>
          <Select.Option value="CANCELLED">已取消</Select.Option>
        </Select>
        
        <Select
          placeholder="筛选优先级"
          allowClear
          style={{ width: 120 }}
          onChange={setPriorityFilter}
        >
          <Select.Option value="HIGH">高</Select.Option>
          <Select.Option value="MEDIUM">中</Select.Option>
          <Select.Option value="LOW">低</Select.Option>
        </Select>
      </div>

      <List
        grid={{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 3, xxl: 4 }}
        dataSource={filteredTasks}
        loading={loading}
        renderItem={(task) => (
          <List.Item>
            <Card
              className={`task-card priority-${task.priority.toLowerCase()} ${
                task.status === 'COMPLETED' ? 'status-completed' : ''
              }`}
              actions={[
                <Button
                  type="text"
                  icon={<EditOutlined />}
                  onClick={() => handleEdit(task)}
                />,
                <Popconfirm
                  title="确定要删除这个任务吗？"
                  onConfirm={() => handleDelete(task.id!)}
                  okText="确定"
                  cancelText="取消"
                >
                  <Button type="text" icon={<DeleteOutlined />} danger />
                </Popconfirm>,
              ]}
            >
              <Meta
                title={task.title}
                description={
                  <div>
                    <p style={{ marginBottom: 8 }}>{task.description}</p>
                    <Space wrap>
                      <Tag color={getStatusColor(task.status)}>
                        {getStatusText(task.status)}
                      </Tag>
                      <Tag color={getPriorityColor(task.priority)}>
                        {getPriorityText(task.priority)}
                      </Tag>
                      {task.category && <Tag>{task.category}</Tag>}
                    </Space>
                    <div style={{ marginTop: 8, fontSize: 12, color: '#666' }}>
                      <div>开始: {dayjs(task.startTime).format('MM-DD HH:mm')}</div>
                      <div>结束: {dayjs(task.endTime).format('MM-DD HH:mm')}</div>
                    </div>
                  </div>
                }
              />
            </Card>
          </List.Item>
        )}
      />

      <TaskFormModal
        visible={modalVisible}
        task={editingTask}
        onCancel={() => {
          setModalVisible(false);
          setEditingTask(undefined);
        }}
        onSuccess={() => {
          setModalVisible(false);
          setEditingTask(undefined);
          loadTasks();
        }}
      />
    </div>
  );
};

export default TaskList;