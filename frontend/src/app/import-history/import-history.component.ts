import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { saveAs } from 'file-saver';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import { ImportHistoryService } from './import-history';

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
    AsyncPipe,
    NgIf
  ],
  styleUrls: ['./import-history.component.css']
})
export class ImportHistoryComponent implements OnInit {
  importHistory$: any;
  isAdmin: boolean = false;

  constructor(
    private http: HttpClient,
    private importHistoryService: ImportHistoryService
  ) {
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

  downloadFile(fileId: number): void {
    const url = `/api/download/${fileId}`;
    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: (response) => {
        saveAs(response, `import-${fileId}.json`);
      },
      error: (error) => {
        console.error('Failed to download file:', error);
      }
    });
  }
}
