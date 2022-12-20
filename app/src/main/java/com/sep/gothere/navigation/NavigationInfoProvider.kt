package com.sep.gothere.navigation

interface NavigationInfoProvider {

    fun getNavigationRoot(): NavigationRoot

    fun getNavigationBranch(): NavigationBranch

    fun getNavigationTag(): NavigationTag

    fun isNavigationBranchOwner(): Boolean

    fun cacheOnBackPressed(): Boolean

    enum class NavigationRoot {
        ROOT_PRELOG,
        ROOT_POSTLOG
    }

    enum class NavigationBranch(val root: NavigationRoot) {
        BRANCH_WELCOME(NavigationRoot.ROOT_PRELOG),
        BRANCH_EXPLORE(NavigationRoot.ROOT_POSTLOG),
        BRANCH_SEARCH(NavigationRoot.ROOT_POSTLOG),
        BRANCH_PROFILE(NavigationRoot.ROOT_POSTLOG),
        BRANCH_BUSINESS(NavigationRoot.ROOT_POSTLOG)
    }

    enum class NavigationTag(val branch: NavigationBranch) {
        TAG_WELCOME_FRAGMENT(NavigationBranch.BRANCH_WELCOME),
        TAG_SIGN_UP_FRAGMENT(NavigationBranch.BRANCH_WELCOME),
        TAG_EXPLORE_FRAGMENT(NavigationBranch.BRANCH_EXPLORE),
        TAG_SEARCH_FRAGMENT(NavigationBranch.BRANCH_SEARCH),
        TAG_PROFILE_FRAGMENT(NavigationBranch.BRANCH_PROFILE),
        TAG_BUSINESS_FRAGMENT(NavigationBranch.BRANCH_BUSINESS)
    }
}