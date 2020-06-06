import name.wildswift.mapache.dafaults.EmptyStateTransition
import name.wildswift.sunrisealarm.changelocation.states.NoActionsState
import name.wildswift.sunrisealarm.changelocation.states.SearchByNameState
import name.wildswift.sunrisealarm.changelocation.states.SelectByNameState
import name.wildswift.sunrisealarm.model.Alarm
import name.wildswift.sunrisealarm.navigation.events.DoAfter
import name.wildswift.sunrisealarm.navigation.states.AboutScreenState
import name.wildswift.sunrisealarm.navigation.states.ClassicDetailsScreenState
import name.wildswift.sunrisealarm.navigation.states.InitialState
import name.wildswift.sunrisealarm.navigation.states.RequestLocationPermissionsDialogState
import name.wildswift.sunrisealarm.navigation.states.RequestLocationPermissionsState
import name.wildswift.sunrisealarm.navigation.states.SelectLocationScreenState
import name.wildswift.sunrisealarm.navigation.states.SetupScreenState
import name.wildswift.sunrisealarm.navigation.transitions.AboutToPrimaryTransition
import name.wildswift.sunrisealarm.navigation.transitions.PrimaryToAboutTransition
import name.wildswift.sunrisealarm.navigation.transitions.PrimaryToRequestLocationDialogTransition
import name.wildswift.sunrisealarm.navigation.transitions.RequestLocationDialogToPrimaryTransition
import name.wildswift.sunrisealarm.navigation.transitions.SplashToPrimaryTransition
import name.wildswift.sunrisealarm.services.location.LocationInfo


basePackageName "name.wildswift.testapp.generated"
statesPackageName ".gen"

actions {
    AppLoaded()
    OpenAlarmDetails(Alarm)
    AboutDeveloper()
    OpenRequestLocationPermissionDialog(DoAfter)
    RequestLocationPermission(DoAfter)
    SelectLocation(DoAfter)
    // location select screen
    MapClicked(LocationInfo)
    AutoSearchEnable()
    AutoSearchDisable()
    SearchText(String)
    LocationFinded(List)
    LocationSelected()

}

layer {
    from(InitialState) {
        when AppLoaded go SetupScreenState with SplashToPrimaryTransition
    }
    $(SetupScreenState) {
        singleInBackStack true

        when OpenAlarmDetails go ClassicDetailsScreenState with EmptyStateTransition
        when OpenRequestLocationPermissionDialog go RequestLocationPermissionsDialogState with PrimaryToRequestLocationDialogTransition
        when AboutDeveloper go AboutScreenState with PrimaryToAboutTransition
    }
    $(ClassicDetailsScreenState) {

    }

    $(AboutScreenState) {
        go SetupScreenState with AboutToPrimaryTransition
    }

    $(RequestLocationPermissionsDialogState) {
        when RequestLocationPermission go RequestLocationPermissionsState with RequestLocationDialogToPrimaryTransition
        go SetupScreenState with RequestLocationDialogToPrimaryTransition
    }

    $(RequestLocationPermissionsState) {
        when SelectLocation go SelectLocationScreenState with EmptyStateTransition
        go SetupScreenState with EmptyStateTransition
    }

    $(SelectLocationScreenState) {
        hasBackStack true

        rootView 1: LocationSelectMapView

        all {
            when SearchText go SearchByNameState with EmptyStateTransition
        }

        from(NoActionsState) {

        }

        $(SearchByNameState) {
            when LocationFinded go SelectByNameState with EmptyStateTransition
        }

        $(SelectByNameState) {
            when LocationSelected go NoActionsState with EmptyStateTransition
        }
    }
}