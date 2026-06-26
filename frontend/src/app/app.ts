import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';

type ApiStatus = 'inconnu' | 'UP' | 'DOWN';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private readonly http = inject(HttpClient);

  protected readonly title = signal('Simulateur de Plateforme Agréée');
  protected readonly apiStatus = signal<ApiStatus>('inconnu');

  constructor() {
    this.http.get<{ status: string }>('/actuator/health').subscribe({
      next: (res) => this.apiStatus.set(res.status === 'UP' ? 'UP' : 'DOWN'),
      error: () => this.apiStatus.set('DOWN')
    });
  }
}
