import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-file-loader',
  templateUrl: './file-loader.component.html',
  standalone: true,
  imports: [
    NgIf
  ],
  styleUrls: ['./file-loader.component.css']
})
export class FileLoaderComponent {
  fileName: string = '';
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private http: HttpClient) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files?.length) {
      this.errorMessage = 'Please choose file';
      return;
    }

    const file = input.files[0];
    this.fileName = file.name;

    input.value = '';

    if (file.type !== 'application/json') {
      this.errorMessage = 'File should be json format';
      return;
    }

    this.errorMessage = '';

    const reader = new FileReader();
    reader.onload = () => {
      try {
        const data = JSON.parse(reader.result as string);

        if (!Array.isArray(data)) {
          throw new Error('Expected json файл');
        }

        this.uploadData(data);
      } catch (e) {
        this.errorMessage = `Error parsing file: ${(e as Error).message}`;
      }
    };
    reader.readAsText(file);
  }

  uploadData(data: any[]): void {
    this.http.post(`http://localhost:2580/api/upload/${sessionStorage.getItem('loggedInUserEmail')}`, data).subscribe({
      next: () => {
        this.successMessage = 'Data loaded successfully';
      },
      error: (error) => {
        this.errorMessage = `Error loading data: ${error.message}`;
      }
    });
  }
}
