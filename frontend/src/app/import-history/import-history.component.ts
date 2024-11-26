import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {AsyncPipe, NgForOf} from '@angular/common';
import {BehaviorSubject} from 'rxjs';
import {ImportHistoryService} from './import-history';

interface ImportHistory {
  id: number;
  status: string;
  username: string;
  countObjects?: number;
}

@Component({
  selector: 'app-import-history',
  templateUrl: './import-history.component.html',
  standalone: true,
  imports: [
    NgForOf,
    AsyncPipe
  ],
  styleUrls: ['./import-history.component.css']
})
export class ImportHistoryComponent implements OnInit {
  importHistory$: any
  isAdmin: boolean = false;

  constructor(private http: HttpClient, private importHistoryService: ImportHistoryService) {
    this.importHistory$ = this.importHistoryService.history$;
  }

  ngOnInit(): void {
    this.fetchImportHistory();
  }

  fetchImportHistory(): void {
    const url = this.isAdmin
      ? '/api/history/all'
      : `/api/history/user/${sessionStorage.getItem('loggedInUserEmail')}`;
    this.http.get<ImportHistory[]>(url).subscribe(
      (data) => this.importHistoryService.setHistory(data),
      (error) => console.error('Error fetching import history:', error)
    );
  }
}
