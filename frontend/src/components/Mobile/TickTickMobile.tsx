import { useState, useEffect, useRef } from 'react';
import { Button, message, Modal, Input } from 'antd';
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
    if (!newTaskTitle.trim()) {
      message.warning('请输入任务标题');
      return;
    }

    try {
      const newTask = {
        title: newTaskTitle,
        description: '',
        startTime: dayjs().toISOString(),
        endTime: dayjs().add(1, 'day').toISOString(),
        priority: 2
      };

      await taskService.createTask(newTask);
      setNewTaskTitle('');
      setTaskModalVisible(false);
      await loadTasks(true); // 强制刷新
      message.success('任务添加成功');
    } catch (error) {
      console.error('添加任务失败:', error);
      message.error('添加任务失败');
    }
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
          setTaskModalVisible(false);
          setNewTaskTitle('');
        }}
        okText="添加"
        cancelText="取消"
      >
        <Input
          placeholder="输入任务标题"
          value={newTaskTitle}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setNewTaskTitle(e.target.value)}
          onPressEnter={handleAddTask}
        />
      </Modal>

      {/* 侧边栏 */}
      <MobileSidebar
        visible={sidebarVisible}
        onClose={() => setSidebarVisible(false)}
        onMenuSelect={(key) => setCurrentView(key)}
      />
    </div>
  );
};

export default TickTickMobile;