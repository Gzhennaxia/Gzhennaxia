export interface Task {
  id?: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  category?: string;
  tags?: string;
  createTime?: string;
  updateTime?: string;
}

export interface TaskFormData {
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  category?: string;
  tags?: string;
}

export type ViewType = 'day' | 'week' | 'month' | 'multiDay' | 'multiWeek';