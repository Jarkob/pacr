import { Subscription, interval } from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { StringService } from './../services/strings.service';
import { RepositoryService } from './../services/repository.service';
import { Repository } from '../classes/repository';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-repositories',
  templateUrl: './admin-repositories.component.html',
  styleUrls: ['./admin-repositories.component.css']
})
export class AdminRepositoriesComponent implements OnInit {

  constructor(
    private stringService: StringService,
    private repositoryService: RepositoryService
  ) { }

  public editRepositoryForm: FormGroup;

  editNameControl: FormControl;
  editPullURLControl: FormControl;
  editObserveAllControl: FormControl;
  editObserveFromDateControl: FormControl;
  editWebHookControl: FormControl;
  editTrackModeControl: FormControl;
  editColorControl: FormControl;
  editTrackedBranchesControl: FormControl;

  allEditBranches: string[] = [];

  public addRepositoryForm: FormGroup;

  addNameControl: FormControl;
  addPullURLControl: FormControl;
  addObserveAllControl: FormControl;
  addObserveFromDateControl: FormControl;
  addWebHookControl: FormControl;
  addTrackModeControl: FormControl;
  addTrackedBranchesControl: FormControl;

  allAddBranches: string[] = [];

  strings: any;

  repositories: Repository[];

  selectedRepository: Repository;

  pullInterval: number;

  repositorySubscription: Subscription;
  repositoryUpdateInterval = 30;

  ngOnInit() {
    this.stringService.getAdminRepositoriesStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    // initial load
    this.repositoryService.getAllRepositories().subscribe(
      data => {
        this.repositories = data;
      }
    );
    this.repositoryService.getPullInterval().subscribe(data => {
      this.pullInterval = data;
    });

    this.repositorySubscription = interval(this.repositoryUpdateInterval * 1000).subscribe(
      val => {
        this.repositoryService.getAllRepositories().subscribe(
          data => {
            this.repositories = data;
          }
        );
        this.repositoryService.getPullInterval().subscribe(data => {
          this.pullInterval = data;
        });
      }
    );

    this.initAddFormControls();
    this.initEditFormControls();
  }

  private initEditFormControls() {
    this.editNameControl = new FormControl(null, [Validators.required]);
    this.editPullURLControl = new FormControl(null, [Validators.required]);
    this.editObserveAllControl = new FormControl(null, [Validators.required]);
    this.editObserveFromDateControl = new FormControl(null, [Validators.required]);
    this.editWebHookControl = new FormControl(null, [Validators.required]);
    this.editTrackModeControl = new FormControl(null, [Validators.required]);
    this.editColorControl = new FormControl(null, [Validators.required]);
    this.editTrackedBranchesControl = new FormControl(null);

    this.editRepositoryForm = new FormGroup({
      name: this.editNameControl,
      pullURL: this.editPullURLControl,
      observeAll: this.editObserveAllControl,
      observeFromDate: this.editObserveFromDateControl,
      webHook: this.editWebHookControl,
      trackMode: this.editTrackModeControl,
      color: this.editColorControl,
      trackedBranches: this.editTrackedBranchesControl
    });
  }

  private initAddFormControls() {
    this.addNameControl = new FormControl('', [Validators.required]);
    this.addPullURLControl = new FormControl('', [Validators.required]);
    this.addObserveAllControl = new FormControl(true, [Validators.required]);
    this.addObserveFromDateControl = new FormControl(new Date(), [Validators.required]);
    this.addWebHookControl = new FormControl(false, [Validators.required]);
    this.addTrackModeControl = new FormControl(true, [Validators.required]);
    this.addTrackedBranchesControl = new FormControl([]);

    this.addRepositoryForm = new FormGroup({
      name: this.addNameControl,
      pullURL: this.addPullURLControl,
      observeAll: this.addObserveAllControl,
      observeFromDate: this.addObserveFromDateControl,
      webHook: this.addWebHookControl,
      trackMode: this.addTrackModeControl,
      trackedBranches: this.addTrackedBranchesControl
    });

    this.allAddBranches = [];
  }

  public hasEditError = (controlName: string, errorName: string) => {
    return this.editRepositoryForm.controls[controlName].hasError(errorName);
  }

  public hasAddError = (controlName: string, errorName: string) => {
    return this.addRepositoryForm.controls[controlName].hasError(errorName);
  }

  public onCancelEditRepository() {
    this.loadRepositoryData(this.selectedRepository);
  }

  public onEditPullURLChange() {
    this.repositoryService.getAllBranches(this.editPullURLControl.value).subscribe(
      data => {
        this.allEditBranches = data;
      }
    );
  }

  public onCancelAddRepository() {
    this.initAddFormControls();
  }

  public onAddPullURLChange() {
    this.repositoryService.getAllBranches(this.addPullURLControl.value).subscribe(
      data => {
        this.allAddBranches = data;
      }
    );
  }

  public addRepository = (addRepositoryFormValue) => {
    if (this.addRepositoryForm.valid) {
      this.repositoryService.addRepository({
        id: addRepositoryFormValue.id,
        trackAllBranches: addRepositoryFormValue.trackAllBranches,
        selectedBranches: addRepositoryFormValue.selectedBranches,
        pullURL: addRepositoryFormValue.pullURL,
        name: addRepositoryFormValue.name,
        hookSet: addRepositoryFormValue.hookSet,
        color: addRepositoryFormValue.color,
        observeFromDate: addRepositoryFormValue.observeFromDate,
        commitLinkPrefix: addRepositoryFormValue.commitLinkPrefix,
        commits: []
      }).subscribe();
    }
  }

  public editRepository = (editRepositoryFormValue) => {
    if (this.editRepositoryForm.valid) {
      this.repositoryService.updateRepository({
        id: editRepositoryFormValue.id,
        trackAllBranches: editRepositoryFormValue.trackAllBranches,
        selectedBranches: editRepositoryFormValue.selectedBranches,
        pullURL: editRepositoryFormValue.pullURL,
        name: editRepositoryFormValue.name,
        hookSet: editRepositoryFormValue.hookSet,
        color: editRepositoryFormValue.color,
        observeFromDate: editRepositoryFormValue.observeFromDate,
        commitLinkPrefix: editRepositoryFormValue.commitLinkPrefix,
        commits: []
      }).subscribe();
    }
  }

  public deleteRepository() {
    this.repositoryService.removeRepository(this.selectedRepository.id);
  }

  public selectRepository(repository: Repository) {
    this.selectedRepository = repository;
    this.allEditBranches = [];

    this.repositoryService.getAllBranches(repository.pullURL).subscribe(
      data => {
        this.allEditBranches = data;
      }
    );

    this.loadRepositoryData(this.selectedRepository);
  }

  private loadRepositoryData(repository: Repository) {
    this.editNameControl.setValue(repository.name);
    this.editPullURLControl.setValue(repository.pullURL);
    this.editObserveAllControl.setValue(repository.observeFromDate === null);
    this.editObserveFromDateControl.setValue(repository.observeFromDate);
    this.editWebHookControl.setValue(repository.hookSet);
    this.editTrackModeControl.setValue(repository.trackAllBranches);
    this.editColorControl.setValue(repository.color);
    this.editTrackedBranchesControl.setValue(repository.selectedBranches);
  }

  public onEditTrackModeChange() {
    const currentSelectedBranches: string[] = this.editTrackedBranchesControl.value;

    this.editTrackedBranchesControl.setValue(this.invertList(currentSelectedBranches, this.allEditBranches));
  }

  public onAddTrackModeChange() {
    const currentSelectedBranches: string[] = this.addTrackedBranchesControl.value;

    this.addTrackedBranchesControl.setValue(this.invertList(currentSelectedBranches, this.allAddBranches));
  }

  public savePullInterval() {
    this.repositoryService.setPullInterval(this.pullInterval).subscribe();
  }

  /**
   * Return a list that only contains values which are in compareTo but not in toInvert.
   * @param toInvert string array
   * @param compareTo string array
   */
  private invertList(toInvert: string[], compareTo: string[]): string[] {
    const newSelectedBranches: string[] = [];

    compareTo.forEach(branch => {
      const foundElement: string = toInvert.find(element => {
        return element === branch;
      });

      if (!foundElement) {
        newSelectedBranches.push(branch);
      }
    });

    return newSelectedBranches;
  }
}
