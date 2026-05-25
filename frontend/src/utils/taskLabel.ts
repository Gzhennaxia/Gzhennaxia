import { Task } from '../types/Task';

/** 将任务优先级（字符串或数字字典值）转为展示用语义字符串 */
export function toPriorityLabel(priority: Task['priority']): string {
  if (typeof priority === 'string') {
    return priority;
  }
  const map: Record<number, string> = { 1: 'LOW', 2: 'MEDIUM', 3: 'HIGH' };
  return map[priority] ?? String(priority);
}

/** 将任务状态（字符串或数字字典值）转为展示用语义字符串 */
export function toStatusLabel(status: Task['status']): string {
  if (typeof status === 'string') {
    return status;
  }
  const map: Record<number, string> = {
    0: 'PENDING',
    1: 'IN_PROGRESS',
    2: 'COMPLETED',
    3: 'CANCELLED',
  };
  return map[status] ?? String(status);
}
