import { useState, useEffect, useRef } from 'react';
import { Button, message, Modal, Input, Form } from 'antd';
import { 
  PlusOutlined, 
  MenuOutlined,
  MoreOutlined,
  LeftOutlined,
  DownOutlined
} from '@ant-design/icons';
import dayjs from 'dayjs';
import { taskService } from '../../services/taskService';
import { Task } from '../../types/Task';
import MobileSidebar from './MobileSidebar';
import InboxView from './InboxView';
import MobileDateTimePicker from './MobileDateTimePicker';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './TickTickMobile.css';

const TickTickMobile: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(false);
  const [isInitialized, setIsInitialized] = useState(false);
  const loadingRef = useRef(false);
  const mountedRef = useRef(false);
  const [taskModalVisible, setTaskModalVisible] = useState(false);
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskDescription, setNewTaskDescription] = useState('');
  const [newTaskStartTime, setNewTaskStartTime] = useState<dayjs.Dayjs | null>(dayjs());
  const [newTaskEndTime, setNewTaskEndTime] = useState<dayjs.Dayjs | null>(dayjs().add(1, 'hour'));
  const [form] = Form.useForm();
  
  // 移动端日期时间选择器状态
  const [datePickerVisible, setDatePickerVisible] = useState(false);
  const [datePickerType, setDatePickerType] = useState<'start' | 'end'>('start');
  const [sidebarVisible, setSidebarVisible] = useState(false);
  const [currentView, setCurrentView] = useState('today');
  const [sectionExpanded, setSectionExpanded] = useState({
    overdue: false,
    today: true,
    completed: false
  });
  const [taskStats, setTaskStats] = useState({
    overdue: 0,
    today: 0,
    completed: 0,
    total: 0
  });

  // 加载任务数据
  const loadTasks = async (force = false) => {
    // 使用ref进行更强的防重复检查
    if (loadingRef.current && !force) {
      console.log('请求正在进行中，跳过重复请求');
      return;
    }
    
    // 如果组件已卸载，不执行请求
    if (!mountedRef.current && !force) {
      console.log('组件已卸载，跳过请求');
      return;
    }
    
    try {
      loadingRef.current = true;
      setLoading(true);
      console.log('开始加载任务数据...');
      
      const [tasksResponse, statsResponse] = await Promise.all([
        taskService.getAllTasks(),
        taskService.getTaskStats()
      ]);
      
      // 再次检查组件是否已卸载
      if (!mountedRef.current && !force) {
        console.log('组件在请求过程中卸载，忽略响应');
        return;
      }
      
      console.log('加载的任务数据:', tasksResponse);
      console.log('任务统计:', statsResponse);
      if (tasksResponse && tasksResponse.length > 0) {
        console.log('第一个任务的字段:', Object.keys(tasksResponse[0]));
        console.log('第一个任务的数据:', tasksResponse[0]);
        console.log('任务总数:', tasksResponse.length);
      }
      setTasks(tasksResponse || []);
      setTaskStats(statsResponse);
      setIsInitialized(true);
    } catch (error) {
      console.error('加载任务失败:', error);
      if (mountedRef.current) {
        message.error('加载任务失败');
      }
    } finally {
      loadingRef.current = false;
      setLoading(false);
    }
  };

  // 切换任务完成状态
  const handleToggleComplete = async (taskId: number) => {
    try {
      const task = tasks.find(t => t.id === taskId);
      if (!task) return;

      const currentStatus = task.status;
      const isCompleted = currentStatus === 2; // 2=COMPLETED
      const updatedTask = {
        ...task,
        status: isCompleted ? 0 : 2, // 0=PENDING, 2=COMPLETED
        priority: typeof task.priority === 'number' ? task.priority : 2,
        tags: task.tags ? (Array.isArray(task.tags) ? task.tags : [task.tags]) : undefined
      };

      await taskService.updateTask(taskId, updatedTask);
      await loadTasks(true); // 强制刷新
      message.success(updatedTask.status === 2 ? '任务已完成' : '任务已重新激活');
    } catch (error) {
      console.error('更新任务状态失败:', error);
      message.error('更新任务状态失败');
    }
  };

  // 添加新任务
  const handleAddTask = async () => {
    try {
      // 验证表单
      const values = await form.validateFields();
      
      const newTask = {
        title: values.title,
        description: values.description || '',
        startTime: newTaskStartTime ? newTaskStartTime.toISOString() : dayjs().toISOString(),
        endTime: newTaskEndTime ? newTaskEndTime.toISOString() : dayjs().add(1, 'hour').toISOString(),
        priority: 2
      };

      await taskService.createTask(newTask);
      
      // 重置表单和状态
      form.resetFields();
      setNewTaskTitle('');
      setNewTaskDescription('');
      setNewTaskStartTime(dayjs());
      setNewTaskEndTime(dayjs().add(1, 'hour'));
      setTaskModalVisible(false);
      
      await loadTasks(true); // 强制刷新
      message.success('任务添加成功');
    } catch (error: unknown) {
      if (error && typeof error === 'object' && 'errorFields' in error) {
        // 表单验证错误
        message.warning('请完善任务信息');
      } else {
        console.error('添加任务失败:', error);
        message.error('添加任务失败');
      }
    }
  };

  // 打开移动端日期时间选择器
  const openDatePicker = (type: 'start' | 'end') => {
    setDatePickerType(type);
    setDatePickerVisible(true);
  };

  // 处理日期时间选择确认
  const handleDateTimeConfirm = (date: dayjs.Dayjs) => {
    if (datePickerType === 'start') {
      setNewTaskStartTime(date);
      form.setFieldsValue({ startTime: date });
    } else {
      setNewTaskEndTime(date);
      form.setFieldsValue({ endTime: date });
    }
    setDatePickerVisible(false);
  };

  // 处理日期时间选择取消
  const handleDateTimeCancel = () => {
    setDatePickerVisible(false);
  };

  // 格式化时间显示
  const formatTime = (timeStr: string) => {
    return dayjs(timeStr).format('HH:mm');
  };

  // 切换section展开状态
  const toggleSection = (section: 'overdue' | 'today' | 'completed') => {
    setSectionExpanded(prev => ({
      ...prev,
      [section]: !prev[section]
    }));
  };

  useEffect(() => {
    mountedRef.current = true;
    
    // 只在组件首次挂载时加载一次
    if (!isInitialized && !loadingRef.current) {
      console.log('组件挂载，开始加载任务');
      loadTasks();
    }
    
    // 清理函数
    return () => {
      mountedRef.current = false;
      console.log('组件卸载');
    };
  }, []); // 空依赖数组，只在挂载和卸载时执行

  // 分类任务
  const todayTasks = tasks.filter(task => {
    const status = task.status;
    const today = dayjs();
    const todayStart = today.startOf('day'); // 今天00:00
    const todayEnd = today.endOf('day');     // 今天23:59
    const startTime = dayjs(task.startTime);
    const endTime = dayjs(task.endTime);
    
    // 判断逻辑：任务时间范围与今天有重叠，且任务未完成
    const isTaskForToday = (status === 0 || status === 1) && ( // 0=PENDING, 1=IN_PROGRESS
      // 条件1：任务开始时间在今天内
      ((startTime.isAfter(todayStart) || startTime.isSame(todayStart)) && 
       (startTime.isBefore(todayEnd) || startTime.isSame(todayEnd))) ||
      // 条件2：任务结束时间在今天内  
      ((endTime.isAfter(todayStart) || endTime.isSame(todayStart)) && 
       (endTime.isBefore(todayEnd) || endTime.isSame(todayEnd))) ||
      // 条件3：任务覆盖今天（开始时间 < 今天00:00 且 结束时间 > 今天23:59）
      (startTime.isBefore(todayStart) && endTime.isAfter(todayEnd))
    );
    
    return isTaskForToday;
  });

  const completedTasks = tasks.filter(task => {
    const status = task.status;
    return status === 2; // 2=COMPLETED
  });

  // 调试信息
  console.log('所有任务:', tasks);
  console.log('今天任务:', todayTasks);
  console.log('已完成任务:', completedTasks);

  // 渲染任务项
  const renderTaskItem = (task: Task, isCompleted: boolean = false) => {
    const status = task.status;
    const actuallyCompleted = isCompleted || status === 2; // 2=COMPLETED
    const dateToShow = task.endTime; // 使用endTime字段
    
    return (
    <div key={task.id} className={`task-item ${actuallyCompleted ? 'completed' : ''}`}>
      <div className="task-checkbox-wrapper">
        <i 
          className={`bi ${actuallyCompleted ? 'bi-check-circle-fill' : 'bi-circle'}`}
          onClick={() => handleToggleComplete(task.id!)}
          style={{ 
            fontSize: '20px', 
            cursor: 'pointer',
            color: actuallyCompleted ? '#52c41a' : '#d9d9d9'
          }}
        />
      </div>
      <div className="task-content">
        <div className={`task-title ${actuallyCompleted ? 'completed-text' : ''}`}>
          {task.title}
        </div>
      </div>
      <div className="task-right">
        {!actuallyCompleted && (
          <div className="task-time">
            {dateToShow ? formatTime(dateToShow) : ''}
          </div>
        )}
        {actuallyCompleted && (
          <div className="completed-date">
            {dateToShow ? dayjs(dateToShow).format('YYYY/M/D') : ''}
          </div>
        )}
      </div>
    </div>
    );
  };

  return (
    <div className="ticktick-mobile">
      {/* 顶部标题栏 */}
      <div className="mobile-header">
        <div className="header-left">
          <MenuOutlined 
            className="menu-icon" 
            onClick={() => setSidebarVisible(true)}
          />
        </div>
        <div className="header-center">
          <h1 className="page-title">
            {currentView === 'today' ? '今天' : 
             currentView === 'inbox' ? '收集箱' : 
             currentView === 'records' ? '记录' : '今天'}
          </h1>
        </div>
        <div className="header-right">
          <div className="refresh-icon" onClick={() => loadTasks(true)}>🔄</div>
          <MoreOutlined className="more-icon" />
        </div>
      </div>

      {/* 搜索栏 */}
      <div className="search-section">
        <div className="search-input">
          <div className="search-icon">🔍</div>
          <span className="search-placeholder">搜索</span>
        </div>
      </div>

      {/* 主要内容区域 */}
      {currentView === 'inbox' ? (
        <InboxView />
      ) : (
        <div className="task-sections">
          {/* 已过期任务 */}
          <div className="task-section">
            <div className="section-header" onClick={() => toggleSection('overdue')}>
              <span className="section-title">已过期</span>
              <span className="section-action">顺延</span>
              <span className="section-count">{taskStats.overdue}</span>
              <span className="section-arrow">
                {sectionExpanded.overdue ? <DownOutlined /> : <LeftOutlined />}
              </span>
            </div>
          </div>

          {/* 今天任务 */}
          <div className="task-section">
            <div className="section-header" onClick={() => toggleSection('today')}>
              <span className="section-title">今天</span>
              <span className="section-count">{taskStats.today}</span>
              <span className="section-arrow">
                {sectionExpanded.today ? <DownOutlined /> : <LeftOutlined />}
              </span>
            </div>
            {sectionExpanded.today && (
              <div className="task-list">
                {todayTasks.map(task => renderTaskItem(task, false))}
              </div>
            )}
          </div>

          {/* 已完成任务 */}
          <div className="task-section">
            <div className="section-header" onClick={() => toggleSection('completed')}>
              <span className="section-title">已完成</span>
              <span className="section-count">{taskStats.completed}</span>
              <span className="section-arrow">
                {sectionExpanded.completed ? <DownOutlined /> : <LeftOutlined />}
              </span>
            </div>
            {sectionExpanded.completed && (
              <div className="task-list">
                {completedTasks.map(task => renderTaskItem(task, true))}
              </div>
            )}
          </div>

          {/* 查看更多 */}
          <div className="view-more">
            <span>查看更多</span>
          </div>
        </div>
      )}

      {/* 底部导航栏 */}
      <div className="bottom-nav">
        <div className="nav-item active">
          <div className="nav-icon">✓</div>
        </div>
        <div className="nav-item">
          <div className="nav-icon">📁</div>
        </div>
        <div className="nav-item">
          <div className="nav-icon">👤</div>
        </div>
      </div>

      {/* 浮动添加按钮 */}
      <div className="floating-add">
        <Button 
          type="primary" 
          shape="circle" 
          size="large"
          icon={<PlusOutlined />}
          onClick={() => setTaskModalVisible(true)}
          className="add-button"
        />
      </div>

      {/* 添加任务弹窗 */}
      <Modal
        title="添加任务"
        open={taskModalVisible}
        onOk={handleAddTask}
        onCancel={() => {
          form.resetFields();
          setNewTaskTitle('');
          setNewTaskDescription('');
          setNewTaskStartTime(dayjs());
          setNewTaskEndTime(dayjs().add(1, 'hour'));
          setTaskModalVisible(false);
        }}
        okText="添加"
        cancelText="取消"
        width={350}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            title: '',
            description: '',
            startTime: dayjs(),
            endTime: dayjs().add(1, 'hour')
          }}
        >
          <Form.Item
            name="title"
            label="任务标题"
            rules={[{ required: true, message: '请输入任务标题' }]}
          >
            <Input
              placeholder="输入任务标题"
              value={newTaskTitle}
              onChange={(e) => setNewTaskTitle(e.target.value)}
            />
          </Form.Item>

          <Form.Item
            name="description"
            label="任务描述"
          >
            <Input.TextArea
              placeholder="输入任务描述（可选）"
              rows={3}
              value={newTaskDescription}
              onChange={(e) => setNewTaskDescription(e.target.value)}
            />
          </Form.Item>

          <Form.Item
            name="startTime"
            label="开始时间"
            rules={[{ required: true, message: '请选择开始时间' }]}
          >
            <div 
              className="mobile-datetime-input"
              onClick={() => openDatePicker('start')}
            >
              <Input
                placeholder="选择开始时间"
                value={newTaskStartTime ? newTaskStartTime.format('MM-DD HH:mm') : ''}
                readOnly
                style={{ 
                  cursor: 'pointer',
                  backgroundColor: '#fff'
                }}
                suffix={<span style={{ color: '#999' }}>📅</span>}
              />
            </div>
          </Form.Item>

          <Form.Item
            name="endTime"
            label="结束时间"
            rules={[
              { required: true, message: '请选择结束时间' },
              () => ({
                validator() {
                  if (!newTaskStartTime || !newTaskEndTime) {
                    return Promise.resolve();
                  }
                  if (newTaskEndTime.isAfter(newTaskStartTime)) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('结束时间必须晚于开始时间'));
                },
              }),
            ]}
          >
            <div 
              className="mobile-datetime-input"
              onClick={() => openDatePicker('end')}
            >
              <Input
                placeholder="选择结束时间"
                value={newTaskEndTime ? newTaskEndTime.format('MM-DD HH:mm') : ''}
                readOnly
                style={{ 
                  cursor: 'pointer',
                  backgroundColor: '#fff'
                }}
                suffix={<span style={{ color: '#999' }}>📅</span>}
              />
            </div>
          </Form.Item>
        </Form>
      </Modal>

      {/* 侧边栏 */}
      <MobileSidebar
        visible={sidebarVisible}
        onClose={() => setSidebarVisible(false)}
        onMenuSelect={(key) => setCurrentView(key)}
      />

      {/* 移动端日期时间选择器 */}
      <MobileDateTimePicker
        visible={datePickerVisible}
        value={datePickerType === 'start' ? newTaskStartTime : newTaskEndTime}
        onConfirm={handleDateTimeConfirm}
        onCancel={handleDateTimeCancel}
        title={datePickerType === 'start' ? '选择开始时间' : '选择结束时间'}
        showTime={true}
      />
    </div>
  );
};

export default TickTickMobile;