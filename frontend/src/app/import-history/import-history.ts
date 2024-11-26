import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

interface ImportHistory {
  id: number;
  status: string;
  username: string;
  countObjects?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ImportHistoryService {
  private historySubject = new BehaviorSubject<ImportHistory[]>([]);
  history$ = this.historySubject.asObservable();

  setHistory(history: ImportHistory[]): void {
    this.historySubject.next(history);
  }

  addHistoryItem(item: ImportHistory): void {
    const currentHistory = this.historySubject.getValue();
    this.historySubject.next([...currentHistory, item]);
  }
}
