import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { Dictionary, DictionaryService } from '../../../shared/services/dictionary.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-dictionary-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NgbModule
  ],
  templateUrl: './dictionary-list.component.html',
  styleUrls: ['./dictionary-list.component.scss']
})
export class DictionaryListComponent implements OnInit {
  dictionaries: Dictionary[] = [];
  dictionaryType = '';
  loading = true;
  error = '';
  success = '';
  processingId: number | null = null;

  constructor(
    private dictionaryService: DictionaryService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.dictionaryType = params.get('type') || '';
      this.loadDictionaries();
    });
  }

  loadDictionaries(): void {
    this.loading = true;
    this.error = '';

    let observable: Observable<Dictionary[]>;

    switch (this.dictionaryType) {
      case 'categories':
        observable = this.dictionaryService.getTicketCategories();
        break;
      case 'statuses':
        observable = this.dictionaryService.getTicketStatuses();
        break;
      case 'priorities':
        observable = this.dictionaryService.getTicketPriorities();
        break;
      default:
        this.error = 'Nieznany typ słownika';
        this.loading = false;
        return;
    }

    observable.subscribe({
      next: (dictionaries) => {
        this.dictionaries = dictionaries;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Nie udało się załadować słownika';
        this.loading = false;
      }
    });
  }

  getDictionaryTitle(): string {
    switch (this.dictionaryType) {
      case 'categories': return 'Kategorie zgłoszeń';
      case 'statuses': return 'Statusy zgłoszeń';
      case 'priorities': return 'Priorytety zgłoszeń';
      default: return 'Słownik';
    }
  }

  deleteDictionary(id: number): void {
    if (confirm('Czy na pewno chcesz usunąć ten element?')) {
      this.processingId = id;
      let observable: Observable<void>;

      switch (this.dictionaryType) {
        case 'categories':
          observable = this.dictionaryService.deleteTicketCategory(id);
          break;
        case 'statuses':
          observable = this.dictionaryService.deleteTicketStatus(id);
          break;
        case 'priorities':
          observable = this.dictionaryService.deleteTicketPriority(id);
          break;
        default:
          this.error = 'Nieznany typ słownika';
          this.processingId = null;
          return;
      }

      observable.subscribe({
        next: () => {
          this.success = 'Element został usunięty';
          this.loadDictionaries();
          this.processingId = null;
        },
        error: (error) => {
          this.error = error.error?.message || 'Nie udało się usunąć elementu';
          this.processingId = null;
        }
      });
    }
  }
}
