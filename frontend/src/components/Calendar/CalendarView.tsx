import React, { useState, useEffect } from 'react';
import { Calendar, Badge, Select, Card, Button, Space } from 'antd';
import { LeftOutlined, RightOutlined } from '@ant-design/icons';
import dayjs, { Dayjs } from 'dayjs';
import { Task, ViewType } from '../../types/Task';
import { taskService } from '../../services/taskService';
import TaskFormModal from '../TaskForm/TaskFormModal';

const CalendarView: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [currentDate, setCurrentDate] = useState<Dayjs>(dayjs());
  const [viewType, setViewType] = useState<ViewType>('month');
  const [modalVisible, setModalVisible] = useState(false);
  const [, setSelectedDate] = useState<Dayjs>();

  useEffect(() => {
    loadTasks();
  }, [currentDate, viewType]);

  const loadTasks = async () => {
    try {
      let startDate: Dayjs;
      let endDate: Dayjs;

      switch (viewType) {
        case 'day':
          startDate = currentDate.startOf('day');
          endDate = currentDate.endOf('day');
          break;
        case 'week':
          startDate = currentDate.startOf('week');
          endDate = currentDate.endOf('week');
          break;
        case 'month':
          startDate = currentDate.startOf('month');
          endDate = currentDate.endOf('month');
          break;
        case 'multiDay':
          startDate = currentDate;
          endDate = currentDate.add(6, 'day');
          break;
        case 'multiWeek':
          startDate = currentDate.startOf('week');
          endDate = currentDate.add(2, 'week').endOf('week');
          break;
        default:
          startDate = currentDate.startOf('month');
          endDate = currentDate.endOf('month');
      }

      const data = await taskService.getTasksByDateRange(
        startDate.format('YYYY-MM-DD HH:mm:ss'),
        endDate.format('YYYY-MM-DD HH:mm:ss')
      );
      setTasks(data);
    } catch (error) {
      console.error('Load tasks failed:', error);
    }
  };

  const getTasksForDate = (date: Dayjs) => {
    return tasks.filter(task => {
      const taskDate = dayjs(task.startTime);
      return taskDate.format('YYYY-MM-DD') === date.format('YYYY-MM-DD');
    });
  };

  const dateCellRender = (value: Dayjs) => {
    const dayTasks = getTasksForDate(value);
    return (
      <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
        {dayTasks.slice(0, 3).map(task => (
          <li key={task.id} style={{ marginBottom: 2 }}>
            <Badge
              status={getTaskBadgeStatus(task.status)}
              text={
                <span style={{ fontSize: 12 }}>
                  {task.title.length > 10 ? `${task.title.slice(0, 10)}...` : task.title}
                </span>
              }
            />
          </li>
        ))}
        {dayTasks.length > 3 && (
          <li>
            <span style={{ fontSize: 12, color: '#999' }}>
              还有 {dayTasks.length - 3} 个任务...
            </span>
          </li>
        )}
      </ul>
    );
  };

  const getTaskBadgeStatus = (status: string) => {
    switch (status) {
      case 'COMPLETED': return 'success';
      case 'IN_PROGRESS': return 'processing';
      case 'PENDING': return 'warning';
      case 'CANCELLED': return 'error';
      default: return 'default';
    }
  };

  const handleDateSelect = (date: Dayjs) => {
    setSelectedDate(date);
    setModalVisible(true);
  };

  const navigateDate = (direction: 'prev' | 'next') => {
    let newDate: Dayjs;
    
    switch (viewType) {
      case 'day':
        newDate = direction === 'next' 
          ? currentDate.add(1, 'day') 
          : currentDate.subtract(1, 'day');
        break;
      case 'week':
        newDate = direction === 'next' 
          ? currentDate.add(1, 'week') 
          : currentDate.subtract(1, 'week');
        break;
      case 'month':
        newDate = direction === 'next' 
          ? currentDate.add(1, 'month') 
          : currentDate.subtract(1, 'month');
        break;
      case 'multiDay':
        newDate = direction === 'next' 
          ? currentDate.add(7, 'day') 
          : currentDate.subtract(7, 'day');
        break;
      case 'multiWeek':
        newDate = direction === 'next' 
          ? currentDate.add(3, 'week') 
          : currentDate.subtract(3, 'week');
        break;
      default:
        newDate = currentDate;
    }
    
    setCurrentDate(newDate);
  };

  const getDateRangeText = () => {
    switch (viewType) {
      case 'day':
        return currentDate.format('YYYY年MM月DD日');
      case 'week':
        return `${currentDate.startOf('week').format('MM月DD日')} - ${currentDate.endOf('week').format('MM月DD日')}`;
      case 'month':
        return currentDate.format('YYYY年MM月');
      case 'multiDay':
        return `${currentDate.format('MM月DD日')} - ${currentDate.add(6, 'day').format('MM月DD日')}`;
      case 'multiWeek':
        return `${currentDate.startOf('week').format('MM月DD日')} - ${currentDate.add(2, 'week').endOf('week').format('MM月DD日')}`;
      default:
        return '';
    }
  };

  return (
    <div>
      <Card 
        className="calendar-toolbar"
        bodyStyle={window.innerWidth <= 768 ? { padding: '12px' } : {}}
      >
        <div style={{ 
          display: 'flex', 
          flexDirection: window.innerWidth <= 768 ? 'column' : 'row',
          justifyContent: 'space-between', 
          alignItems: 'center',
          gap: window.innerWidth <= 768 ? '12px' : '0'
        }}>
          <Space>
            <Button 
              icon={<LeftOutlined />} 
              onClick={() => navigateDate('prev')}
              size={window.innerWidth <= 768 ? 'middle' : 'middle'}
            />
            <span style={{ 
              fontSize: window.innerWidth <= 768 ? 16 : 18, 
              fontWeight: 'bold', 
              minWidth: window.innerWidth <= 768 ? 150 : 200, 
              textAlign: 'center' 
            }}>
              {getDateRangeText()}
            </span>
            <Button 
              icon={<RightOutlined />} 
              onClick={() => navigateDate('next')}
              size={window.innerWidth <= 768 ? 'middle' : 'middle'}
            />
          </Space>
          
          <Select
            value={viewType}
            onChange={setViewType}
            style={{ 
              width: window.innerWidth <= 768 ? '100%' : 120,
              maxWidth: 200
            }}
          >
            <Select.Option value="day">日视图</Select.Option>
            <Select.Option value="week">周视图</Select.Option>
            <Select.Option value="month">月视图</Select.Option>
            <Select.Option value="multiDay">多日视图</Select.Option>
            <Select.Option value="multiWeek">多周视图</Select.Option>
          </Select>
        </div>
      </Card>

      <Calendar
        value={currentDate}
        onSelect={handleDateSelect}
        dateCellRender={dateCellRender}
        headerRender={() => null} // 隐藏默认头部，使用自定义头部
      />

      <TaskFormModal
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSuccess={() => {
          setModalVisible(false);
          loadTasks();
        }}
      />
    </div>
  );
};

export default CalendarView;