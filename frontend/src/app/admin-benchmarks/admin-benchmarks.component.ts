import { MatSnackBar } from '@angular/material';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { BenchmarkGroup } from './../classes/benchmark-group';
import { Benchmark } from './../classes/benchmark';
import { BenchmarkService } from './../services/benchmark.service';
import { StringService } from './../services/strings.service';
import { Component, OnInit } from '@angular/core';
import { transferArrayItem, CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';

export class Group {
  id: number;
  name: string;
  benchmarks: Benchmark[];
  expanded: boolean;

  constructor(
    id: number,
    name: string
  ) {
    this.id = id;
    this.name = name;
    this.expanded = false;
   }
}

@Component({
  selector: 'app-admin-benchmarks',
  templateUrl: './admin-benchmarks.component.html',
  styleUrls: ['./admin-benchmarks.component.css']
})
export class AdminBenchmarksComponent implements OnInit {

  constructor(
    private benchmarkService: BenchmarkService,
    private stringService: StringService,
    private snackBar: MatSnackBar
  ) { }

  overviewPageIndex = 0;
  undefinedGroupId = -1;

  strings: any;

  dropped = false;

  public addBenchmarkGroupForm: FormGroup;
  addGroupNameControl: FormControl;

  public editBenchmarkGroupForm: FormGroup;
  editGroupNameControl: FormControl;

  selectedBenchmarkGroup: Group;

  public editBenchmarkForm: FormGroup;
  editNameControl: FormControl;
  editDescriptionControl: FormControl;
  editGroupControl: FormControl;

  selectedBenchmark: Benchmark;

  benchmarkGroups: Group[] = [];

  ngOnInit() {
    this.stringService.getAdminBenchmarksStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    this.getBenchmarkGroups();

    this.initEditBenchmarkFormControls();
    this.initAddGroupFormControls();
    this.initEditGroupFormControls();
  }

  async getBenchmarkGroups() {
    this.benchmarkGroups = [];

    const groups: BenchmarkGroup[] = await this.benchmarkService.getAllGroups().toPromise();

    groups.forEach(group => {
      const benchmarkGroup: Group = new Group(group.id, group.name);

      this.benchmarkService.getBenchmarksByGroup(group.id).subscribe(
        data => {
          benchmarkGroup.benchmarks = data;
        }
      );

      this.benchmarkGroups.push(benchmarkGroup);
    });

    // fetch all benchmarks which don't belong to a group.
    const undefinedGroupBenchmarks = await this.benchmarkService.getBenchmarksByGroup(-1).toPromise();

    // add a special group if benchmarks without a group exist.
    if (undefinedGroupBenchmarks && undefinedGroupBenchmarks.length > 0) {
      const undefinedGroup: Group = new Group(-1, 'Undefined Group');
      undefinedGroup.benchmarks = undefinedGroupBenchmarks;

      this.benchmarkGroups.push(undefinedGroup);
    }
  }

  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      this.dropped = true;
      transferArrayItem(event.previousContainer.data,
                        event.container.data,
                        event.previousIndex,
                        event.currentIndex);
    }
  }

  hasEditError = (controlName: string, errorName: string) => {
    return this.editBenchmarkForm.controls[controlName].hasError(errorName);
  }

  hasAddGroupError = (controlName: string, errorName: string) => {
    return this.addBenchmarkGroupForm.controls[controlName].hasError(errorName);
  }

  hasEditGroupError = (controlName: string, errorName: string) => {
    return this.editBenchmarkGroupForm.controls[controlName].hasError(errorName);
  }

  initEditBenchmarkFormControls() {
    this.editNameControl = new FormControl('', [Validators.required]);
    this.editDescriptionControl = new FormControl('', [Validators.required]);
    this.editGroupControl = new FormControl(null, [Validators.required]);

    this.editBenchmarkForm = new FormGroup({
      name: this.editNameControl,
      description: this.editDescriptionControl,
      group: this.editGroupControl
    });
  }

  toggleGroup(group: Group) {
    group.expanded = !group.expanded;
  }

  selectBenchmark(benchmark: Benchmark) {
    this.selectedBenchmark = benchmark;
    this.loadBenchmarkData(benchmark);
  }

  loadBenchmarkData(benchmark: any) {
    console.log(benchmark);
    this.editNameControl.setValue(benchmark.customName);
    this.editDescriptionControl.setValue(benchmark.description);

    // I dont know why the group field is called "group" when it should be "benchmarkGroup"
    this.editGroupControl.setValue(benchmark.group.id);
  }

  onCancelEditBenchmark() {
    this.loadBenchmarkData(this.selectedBenchmark);
  }

  selectBenchmarkGroup(group: Group) {
    this.selectedBenchmarkGroup = group;
    this.loadBenchmarkGroupData(this.selectedBenchmarkGroup);
  }

  initEditGroupFormControls() {
    this.editGroupNameControl = new FormControl('', [Validators.required]);

    this.editBenchmarkGroupForm = new FormGroup({
      name: this.editGroupNameControl
    });
  }

  loadBenchmarkGroupData(benchmarkGroup: Group) {
    this.editGroupNameControl.setValue(benchmarkGroup.name);
  }

  editBenchmark(editBenchmarkFormValue: any) {
    this.benchmarkService.updateBenchmark({
      id: this.selectedBenchmark.id,
      customName: editBenchmarkFormValue.name,
      description: editBenchmarkFormValue.description,
      groupId: editBenchmarkFormValue.group
    }).subscribe(
      data => {
        this.openSnackBar(this.strings.editSuccess);
        this.initEditBenchmarkFormControls();
        this.getBenchmarkGroups();
      },
      err => {
        this.openSnackBar(this.strings.snackBarError);
      }
    );
  }

  addBenchmarkGroup(form: any) {
    this.benchmarkService.addGroup(form.name).subscribe(
      data => {
        this.openSnackBar(this.strings.addSuccess);
        this.initAddGroupFormControls();
        this.getBenchmarkGroups();
      },
      err => {
        this.openSnackBar(this.strings.snackBarError);
      }
    );
  }

  editBenchmarkGroup(editBenchmarkGroupFormValue: any) {
    this.benchmarkService.updateGroup({
      id: this.selectedBenchmarkGroup.id,
      name: editBenchmarkGroupFormValue.name
    }).subscribe(
      data => {
        this.openSnackBar(this.strings.editSuccess);
        this.initEditGroupFormControls();
        this.getBenchmarkGroups();
        this.selectedBenchmarkGroup = null;
      },
      err => {
        this.openSnackBar(this.strings.snackBarError);
      }
    );
  }

  deleteBenchmarkGroup(id: number) {
    this.benchmarkService.deleteGroup(id).subscribe(
      data => {
        this.openSnackBar(this.strings.deleteSuccess);
        this.selectedBenchmarkGroup = null;
        this.getBenchmarkGroups();
      },
      err => {
        this.openSnackBar(this.strings.snackBarError);
      }
    );
  }

  onCancelEditBenchmarkGroup() {
    this.loadBenchmarkGroupData(this.selectedBenchmarkGroup);
  }

  onSaveBenchmarkGroups() {
    this.dropped = false;

    for (const group of this.benchmarkGroups) {
      for (const benchmark of group.benchmarks) {
        this.benchmarkService.updateBenchmark({
          id: benchmark.id,
          customName: benchmark.customName,
          description: benchmark.description,
          groupId: group.id
        }).subscribe();
      }
    }

    this.getBenchmarkGroups();
  }

  onCancelBenchmarkGroups() {
    this.dropped = false;
    this.getBenchmarkGroups();
  }

  initAddGroupFormControls() {
    this.addGroupNameControl = new FormControl('', [Validators.required]);

    this.addBenchmarkGroupForm = new FormGroup({
      name: this.addGroupNameControl
    });
  }

  onCancelAddBenchmarkGroup() {
    this.initAddGroupFormControls();
  }

  openSnackBar(message: string) {
    const snackBarDuration = 2000;

    this.snackBar.open(message, this.strings.snackBarAction, {
      duration: snackBarDuration,
    });
  }
}
