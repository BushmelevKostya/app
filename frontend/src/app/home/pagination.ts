import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject} from "rxjs";

@Injectable()
export class Pagination{
    constructor(private http: HttpClient) {
    }

    apiUrl = "http://localhost:2580/api/action"
    public movies = new BehaviorSubject<any[]>([]);
    public allMovies = new BehaviorSubject<any[]>([]);
    totalItemsCount: number = 0;
    totalPages: number = 0;
    currentPage: number = 1;
    itemsPerPage: number = 5;

    getCountMovies() {
        this.http.get<any>(`${this.apiUrl}/count`).subscribe((data: any) => {
            this.totalItemsCount = data;
            this.totalPages = Math.ceil(this.totalItemsCount / this.itemsPerPage);
        });
    }

    loadMovies() {
        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;
        this.http.get<any[]>(`${this.apiUrl}/${startIndex}/${endIndex}`).subscribe((data: any[]) => {
            this.movies.next(data)
            this.allMovies.next(data);
        });
    }

    goToPage(page: number): void {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
            this.loadMovies();
        }
    }

    nextPage(): void {
        if (this.currentPage < this.totalPages) {
            this.currentPage++;
            this.loadMovies();
        }
    }

    previousPage(): void {
        if (this.currentPage > 1) {
            this.currentPage--;
            this.loadMovies()   ;
        }
    }

    getPages(): number[] {
        const pages = [];
        for (let i = Math.max(this.currentPage - 2, 1); i <= Math.min(this.totalPages, this.currentPage + 2); i++) {
            pages.push(i);
        }
        return pages;
    }

    get currentPageItemsCount(): number {
        return this.currentPage * this.itemsPerPage;
    }
}
