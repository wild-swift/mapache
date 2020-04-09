import name.wildswift.mapache.generator.dslmodel.Movement
import name.wildswift.sunrisealarm.model.Alarm
import name.wildswift.sunrisealarm.navigation.events.DoAfter
import name.wildswift.sunrisealarm.navigation.states.AboutScreenState
import name.wildswift.sunrisealarm.navigation.states.ClassicDetailsScreenState
import name.wildswift.sunrisealarm.navigation.states.InitialState
import name.wildswift.sunrisealarm.navigation.states.RequestLocationPermissionsDialogState
import name.wildswift.sunrisealarm.navigation.states.SetupScreenState
import android.view.FrameLayout

basePackageName "name.wildswift.sunrisealarm.navigation"
statesPackageName ".gen"

actions {
    packageName = ".events"

    StartApp()
    OpenAlarmDetails(Alarm)
    AppLoaded()
    AboutDeveloper()
    OpenRequestLocationPermissionDialog(DoAfter)
    RequestLocationPermission(DoAfter)
    SelectLocation(DoAfter)
}

layer {
    hasBackStack = true

    from(InitialState) {
        when StartApp go SetupScreenState with Movement
    }

    $(SetupScreenState) {

        when OpenAlarmDetails go ClassicDetailsScreenState with Movement
        when OpenRequestLocationPermissionDialog go RequestLocationPermissionsDialogState with Movement
        when AboutDeveloper go AboutScreenState with Movement

        hasBackStack false
        rootview 0: FrameLayout

        from(ClassicDetailsScreenState) {

        }
    }
    $(ClassicDetailsScreenState) {

    }
    $(RequestLocationPermissionsDialogState) {

    }
    $(AboutScreenState) {

    }
}