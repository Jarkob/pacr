import { ImportExportService } from './../services/import-export.service';
import { Component, OnInit, SecurityContext } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-admin-import-export',
  templateUrl: './admin-import-export.component.html',
  styleUrls: ['./admin-import-export.component.css']
})
export class AdminImportExportComponent implements OnInit {

  constructor(
    private importExportService: ImportExportService,
    private sanitizer: DomSanitizer,
    private datePipe: DatePipe
  ) { }

  file: any;
  loadingImport = false;
  loadingExport = false;

  exportedResults: string;
  exportedFileUrl: SafeResourceUrl;

  ngOnInit() {

  }

  fileChanged(e) {
    this.file = e.target.files[0];
    console.log(this.file);
  }

  import() {
    this.loadingImport = true;
    const fileReader = new FileReader();

    fileReader.onload = (e) => {
      this.importExportService.import(fileReader.result).subscribe(
        data => {
          this.loadingImport = false;
        },
        err => {
          this.loadingImport = false;
        }
      );
    };

    fileReader.readAsText(this.file);
  }

  export() {
    this.loadingExport = true;

    this.importExportService.export().subscribe(
      data => {
        this.exportedResults = JSON.stringify(data, null, 2);

        const blob = new Blob([this.exportedResults], { type: 'application/json' });

        this.exportedFileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));

        this.loadingExport = false;
      }
    );
  }

  getExportFileName() {
    const date = this.datePipe.transform(new Date(), 'yyyy-MM-dd');
    return date + '-pacr-export.json';
  }

}
