import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ITag } from '../tag.model';

@Component({
  selector: 'jhi-tag-detail',
  templateUrl: './tag-detail.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TagDetailComponent {
  @Input() tag: ITag | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
