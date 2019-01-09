package com.hubofallthings.dataplugs.services

import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.objects.extrernalapps.HATApplicationObject
import com.hubofallthings.android.hatApi.services.HATExternalAppsService

class ExternalAppsServices{
    fun getApps(kind : String ,userToken : String ,userDomain : String,completion: ((List<HATApplicationObject>?, String?)-> Unit),failCallBack : (((HATError) -> Unit))){
        if(kind == "App"){
            HATExternalAppsService().getApps(userToken,userDomain,{list: List<HATApplicationObject>?, s: String? -> completion(list,s)},{ error -> failCallBack(error) })
        }else if (kind == "DataPlug"){
            HATExternalAppsService().getDataPlugs(userToken,userDomain,{list: List<HATApplicationObject>?, s: String? -> completion(list,s)},{ error -> failCallBack(error) })
        }
    }

    /*
    disable app
     */
    fun disableApp(appName : String ,userToken : String ,userDomain : String,completion: ((HATApplicationObject?, String?)-> Unit),failCallBack : (((HATError) -> Unit))){
        HATExternalAppsService().disableApplication(userToken,userDomain,appName,{ app: HATApplicationObject?, s: String? -> completion(app,s)},{ error -> failCallBack(error) })
    }
    /*
    get app info
     */
    fun getAppInfo(appName : String ,userToken : String ,userDomain : String,completion: ((HATApplicationObject?, String?)-> Unit),failCallBack : (((HATError) -> Unit))){
        HATExternalAppsService().getAppWithAppId(userToken,userDomain,appName,{ app: HATApplicationObject?, s: String? -> completion(app,s)},{ error -> failCallBack(error) })
    }

    /**
    Returns a ApplicationConnectionState according to the application state

    - parameter application: The application to check the state of

    - returns: An ApplicationConnectionState case depending in the status of the app
     */
    fun getState(application : HATApplicationObject) : ApplicationConnectionState {
        return if (application.setup && application.needsUpdating != null && application.needsUpdating!!) {
            ApplicationConnectionState.Update
        } else if (application.enabled && !application.active) {
            ApplicationConnectionState.Failing
        } else if (application.enabled && application.active) {
            if(application.mostRecentData != null)
                ApplicationConnectionState.Running
            else
                ApplicationConnectionState.Fetching
        } else {
            ApplicationConnectionState.Untouched
        }
    }
}
enum class ApplicationConnectionState{
    Update,Failing, Running , Fetching , Untouched
}