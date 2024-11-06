import {Component, OnInit, ElementRef, ViewChild} from '@angular/core';
import {WebSocketService} from '../services/websocket';
import {BehaviorSubject} from 'rxjs';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {AuthGuard} from '../auth.guard';
import {Router} from '@angular/router';
import {NgForOf, NgIf} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-visualization',
  templateUrl: './visualization.component.html',
  standalone: true,
  styleUrls: ['./visualization.component.css'],
  imports: [HttpClientModule, NgForOf, NgIf, ReactiveFormsModule]
})
export class VisualizationComponent implements OnInit {
  @ViewChild('canvas', {static: true}) canvas!: ElementRef<HTMLCanvasElement>;
  private ctx!: CanvasRenderingContext2D | null;
  public movies = new BehaviorSubject<any[]>([]);
  public selectedMovie: any = null;
  public showModal: boolean = false;

  constructor(private webSocketService: WebSocketService, private http: HttpClient, private authGuard: AuthGuard, private router: Router, private fb: FormBuilder) {
    this.getMovies();
  }

  coordinatesVisible = false
  directorVisible = false
  screenwriterVisible = false
  operatorVisible = false
  directorLocationVisible = false
  screenwriterLocationVisible = false
  operatorLocationVisible = false
  existingCoordinates: any[] = [];
  existingDirectors: any[] = [];
  existingScreenwriters: any[] = [];
  existingOperators: any[] = [];
  existingLocations: any[] = [];
  selectedMovieId: number | null = null;
  updateFlag = 0
  movieUpdateForm!: FormGroup
  apiUrl = "http://localhost:2580/api/action";

  ngOnInit(): void {
    this.ctx = this.canvas.nativeElement.getContext('2d');

    this.webSocketService.connect();

    this.webSocketService.messages.subscribe((data) => {
      const moviesArray = JSON.parse(data);
      this.movies.next(moviesArray);
      if (Array.isArray(moviesArray)) {
        this.drawMovies(moviesArray);
      }
    });

    this.movieUpdateForm = this.initMovieForm()
  }

  initMovieForm() {
    return this.fb.group({
      name: ['defaultName', [Validators.required, Validators.minLength(1)]],
      creationDate: ["2024-10-15 14:30:55", [Validators.required]],
      oscarsCount: [1, [Validators.required, Validators.min(1)]],
      budget: [1, [Validators.required, Validators.min(1)]],
      totalBoxOffice: [1, [Validators.min(1)]],
      mpaaRating: ['G', [Validators.required]],
      length: [1, [Validators.required, Validators.min(1)]],
      goldenPalmCount: [1, [Validators.min(1)]],
      usaBoxOffice: [1, [Validators.required, Validators.min(1)]],
      tagline: ['defaultTagline', [Validators.required]],
      genre: ['ACTION'],
      coordinates: this.fb.group({
        id: [''],
        x: [0, [Validators.required]],
        y: [0, [Validators.required]]
      }),
      director: this.fb.group({
        id: [''],
        name: ['defaultName', [Validators.required, Validators.minLength(1)]],
        eyeColor: ['BLACK', [Validators.required]],
        hairColor: ['BLACK', [Validators.required]],
        location: this.fb.group({
          id: [''],
          x: [0, [Validators.required]],
          y: [0, [Validators.required]],
          z: [0, [Validators.required]],
          name: ['defaultLocationName', [Validators.required, Validators.minLength(1)]]
        }),
        height: [1, [Validators.required, Validators.min(1)]],
        nationality: ['SPAIN']
      }),
      screenwriter: this.fb.group({
        id: [''],
        name: ['defaultName', [Validators.required, Validators.minLength(1)]],
        eyeColor: ['BLACK', [Validators.required]],
        hairColor: ['BLACK', [Validators.required]],
        location: this.fb.group({
          id: [''],
          x: [0, [Validators.required]],
          y: [0, [Validators.required]],
          z: [0, [Validators.required]],
          name: ['defaultLocationName', [Validators.required, Validators.minLength(1)]]
        }),
        height: [1, [Validators.required, Validators.min(1)]],
        nationality: ['SPAIN']
      }),
      operator: this.fb.group({
        id: [''],
        name: ['defaultName', [Validators.required, Validators.minLength(1)]],
        eyeColor: ['BLACK', [Validators.required]],
        hairColor: ['BLACK', [Validators.required]],
        location: this.fb.group({
          id: [''],
          x: [0, [Validators.required]],
          y: [0, [Validators.required]],
          z: [0, [Validators.required]],
          name: ['defaultLocationName', [Validators.required, Validators.minLength(1)]]
        }),
        height: [1, [Validators.required, Validators.min(1)]],
        nationality: ['SPAIN']
      })
    });
  }

  drawMovies(movies: any[]): void {
    if (this.ctx) {
      this.ctx.clearRect(0, 0, this.canvas.nativeElement.width, this.canvas.nativeElement.height);
      movies.forEach(movie => {
        const color = this.getColorByUser(movie.userId);
        this.drawMovie(movie, color);
      });
    }
  }

  drawMovie(movie: any, color: string): void {
    if (this.ctx) {
      this.ctx.fillStyle = color;
      this.ctx.fillRect(movie.coordinates.x, movie.coordinates.y, 20, 10);

      this.ctx.font = "12px Arial";
      this.ctx.fillStyle = "black";
      this.ctx.fillText(movie.name, movie.coordinates.x, movie.coordinates.y - 5);

      this.canvas.nativeElement.addEventListener('click', (event) => {
        const rect = this.canvas.nativeElement.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;
        if (this.isWithinMovieBounds(x, y, movie)) {
          this.selectedMovie = movie;
          this.showModal = true;
        }
      });
    }
  }

  getColorByUser(userId: number): string {
    const colors = ['red', 'blue', 'green', 'yellow', 'purple'];
    return colors[userId % colors.length];
  }

  isWithinMovieBounds(x: number, y: number, movie: any): boolean {
    return (
      x >= movie.coordinates.x &&
      x <= movie.coordinates.x + 20 &&
      y >= movie.coordinates.y &&
      y <= movie.coordinates.y + 10
    );
  }

  toggleCoordinates() {
    this.coordinatesVisible = !this.coordinatesVisible;
  }

  loadExistingCoordinates() {
    this.http.get('/api/coordinates').subscribe(
      (response: any) => {
        this.existingCoordinates = response;
      },
      (error) => {
        console.error("Error loading coordinates:", error);
      }
    );
  }

  loadExistingPersons() {
    this.http.get('/api/persons').subscribe(
      (response: any) => {
        this.existingDirectors = response;
        this.existingScreenwriters = response;
        this.existingOperators = response;
      },
      (error) => {
        console.error("Error loading persons:", error);
      }
    );
  }

  loadExistingLocations() {
    this.http.get('/api/locations').subscribe(
      (response: any) => {
        this.existingLocations = response;
      },
      (error) => {
        console.error("Error loading locations:", error);
      }
    );
  }

  selectCoordinate(coordinate: any) {
    this.movieUpdateForm.get('coordinates.id')?.setValue(coordinate.id);
    this.movieUpdateForm.get('coordinates.x')?.setValue(coordinate.x);
    this.movieUpdateForm.get('coordinates.y')?.setValue(coordinate.y);
  }

  selectDirector(director: any) {
    this.movieUpdateForm.get('director.id')?.setValue(director.id);
    this.movieUpdateForm.get('director.name')?.setValue(director.name);
    this.movieUpdateForm.get('director.eyeColor')?.setValue(director.eyeColor);
    this.movieUpdateForm.get('director.hairColor')?.setValue(director.hairColor);
    this.movieUpdateForm.get('director.height')?.setValue(director.height);
    this.movieUpdateForm.get('director.nationality')?.setValue(director.nationality);
  }

  selectDirectorLocation(location: any) {
    this.movieUpdateForm.get('director.location.id')?.setValue(location.id);
    this.movieUpdateForm.get('director.location.x')?.setValue(location.x);
    this.movieUpdateForm.get('director.location.y')?.setValue(location.y);
    this.movieUpdateForm.get('director.location.z')?.setValue(location.z);
    this.movieUpdateForm.get('director.location.name')?.setValue(location.name);
  }

  selectScreenwriter(screenwriter: any) {
    this.movieUpdateForm.get('screenwriter.id')?.setValue(screenwriter.id);
    this.movieUpdateForm.get('screenwriter.name')?.setValue(screenwriter.name);
    this.movieUpdateForm.get('screenwriter.eyeColor')?.setValue(screenwriter.eyeColor);
    this.movieUpdateForm.get('screenwriter.hairColor')?.setValue(screenwriter.hairColor);
    this.movieUpdateForm.get('screenwriter.height')?.setValue(screenwriter.height);
    this.movieUpdateForm.get('screenwriter.nationality')?.setValue(screenwriter.nationality);
  }

  selectScreenwriterLocation(location: any) {
    this.movieUpdateForm.get('screenwriter.location.id')?.setValue(location.id);
    this.movieUpdateForm.get('screenwriter.location.x')?.setValue(location.x);
    this.movieUpdateForm.get('screenwriter.location.y')?.setValue(location.y);
    this.movieUpdateForm.get('screenwriter.location.z')?.setValue(location.z);
    this.movieUpdateForm.get('screenwriter.location.name')?.setValue(location.name);
  }

  selectOperator(operator: any) {
    this.movieUpdateForm.get('operator.id')?.setValue(operator.id);
    this.movieUpdateForm.get('operator.name')?.setValue(operator.name);
    this.movieUpdateForm.get('operator.eyeColor')?.setValue(operator.eyeColor);
    this.movieUpdateForm.get('operator.hairColor')?.setValue(operator.hairColor);
    this.movieUpdateForm.get('operator.height')?.setValue(operator.height);
    this.movieUpdateForm.get('operator.nationality')?.setValue(operator.nationality);
  }

  selectOperatorLocation(location: any) {
    this.movieUpdateForm.get('operator.location.id')?.setValue(location.id);
    this.movieUpdateForm.get('operator.location.x')?.setValue(location.x);
    this.movieUpdateForm.get('operator.location.y')?.setValue(location.y);
    this.movieUpdateForm.get('operator.location.z')?.setValue(location.z);
    this.movieUpdateForm.get('operator.location.name')?.setValue(location.name);
  }

  toggleDirector() {
    this.directorVisible = !this.directorVisible;
  }

  toggleDirectorLocation() {
    this.directorLocationVisible = !this.directorLocationVisible;
  }

  toggleScreenwriter() {
    this.screenwriterVisible = !this.screenwriterVisible;
  }

  toggleScreenwriterLocation() {
    this.screenwriterLocationVisible = !this.screenwriterLocationVisible;
  }

  toggleOperator() {
    this.operatorVisible = !this.operatorVisible;
  }

  toggleOperatorLocation() {
    this.operatorLocationVisible = !this.operatorLocationVisible;
  }

  getMovies() {
    this.http.get<any[]>(this.apiUrl).subscribe((data: any[]) => {
      this.movies.next(data);
      this.drawMovies(data);
    });
  }

  navigateTo(address: string) {
    this.authGuard.markProgrammaticNavigation();
    this.router.navigate([address]);
  }

  closeModal() {
    this.showModal = false;
    this.selectedMovie = null;
  }

  refreshMovies() {
    this.getMovies();
  }

  updateMovie() {
    return this.http.put<any[]>(`${this.apiUrl}/${this.selectedMovieId}/${sessionStorage.getItem('loggedInUserEmail')}`, this.movieUpdateForm.value)
      .subscribe((data: any[]) => {
        this.movies.next(data);
        this.changeUpdateFlag();
        this.initVars();
        this.closeModal();
        this.getMovies();
      });
  }

  openUpdateModal(id: any) {
    this.movieUpdateForm = this.initMovieForm()
    this.selectedMovieId = id
    this.changeUpdateFlag()
  }

  changeUpdateFlag() {
    this.movieUpdateForm.get("creationDate")?.setValue(this.setCurrentTime())
    this.updateFlag = this.updateFlag ? 0 : 1
  }

  setCurrentTime(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); // Добавляем 1, так как месяцы от 0 до 11
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }

  initVars() {
    this.coordinatesVisible = false
    this.directorVisible = false
    this.screenwriterVisible = false
    this.operatorVisible = false
    this.directorLocationVisible = false
    this.screenwriterLocationVisible = false
    this.operatorLocationVisible = false

    this.existingCoordinates = [];
    this.existingDirectors = [];
    this.existingScreenwriters = [];
    this.existingOperators = [];
    this.existingLocations = [];

    this.movieUpdateForm.get('coordinates.id')?.setValue('');
    this.movieUpdateForm.get('director.id')?.setValue('');
    this.movieUpdateForm.get('director.location.id')?.setValue('');
    this.movieUpdateForm.get('screenwriter.id')?.setValue('');
    this.movieUpdateForm.get('screenwriter.location.id')?.setValue('');
    this.movieUpdateForm.get('operator.id')?.setValue('');
    this.movieUpdateForm.get('operator.location.id')?.setValue('');
  }

  getFormErrors(command: String): string[] {
    const errors: string[] = [];

    const errorMessages: { [key: string]: string } = {
      required: 'Поле обязательно для заполнения',
      min: 'Значение должно быть больше или равно минимальному допустимому',
      minLength: 'Длина должна быть больше или равна минимальной допустимой'
    };

    function getErrors(control: any, prefix: string) {
      if (control instanceof FormGroup) {
        Object.keys(control.controls).forEach((key) => {
          getErrors(control.get(key), `${prefix}${key}.`);
        });
      } else if (control.errors) {
        Object.keys(control.errors).forEach((errorKey) => {
          const errorMessage = errorMessages[errorKey] || `Ошибка: ${errorKey}`;
          errors.push(`${prefix.slice(0, -1)}: ${errorMessage}`);
        });
      }
    }

    getErrors(this.movieUpdateForm, '')
    return errors;
  }
}
