import { Component, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITag } from '../tag.model';
import { TagService } from '../service/tag.service';

@Component({
  templateUrl: './tag-delete-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [SharedModule, FormsModule],
})
export class TagDeleteDialogComponent {
  tag?: ITag;

  constructor(
    protected tagService: TagService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tagService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
