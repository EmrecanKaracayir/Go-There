package com.sep.gothere.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sep.gothere.R
import com.sep.gothere.features.root_postlog.business.branch_business.ui.BusinessFragment
import com.sep.gothere.features.root_postlog.explore.branch_explore.ui.ExploreFragment
import com.sep.gothere.features.root_postlog.profile.branch_profile.ui.ProfileFragment
import com.sep.gothere.features.root_postlog.search.branch_search.ui.SearchFragment
import com.sep.gothere.features.root_prelog.welcome.leaf_signup.ui.SignUpFragment
import com.sep.gothere.features.root_prelog.welcome.branch_welcome.ui.WelcomeFragment
import com.sep.gothere.util.exhaustive

private const val BASE_FRAGMENT_CONTAINER = R.id.fragment_container

private val navigationStack = ArrayList<NavigationNode>()

var savedLocationIndex = -1

fun loadSession(fragmentManager: FragmentManager) {
    for (navigationNode in navigationStack) navigationNode.location =
        fragmentManager.findFragmentByTag(navigationNode.location.getNavigationTag().name) as NavigationInfoProvider
    navigateToFragment(
        fragmentManager,
        navigationStack[savedLocationIndex].location as Fragment,
        false
    )
}

fun saveLocationIndex(fragmentManager: FragmentManager) {
    val source: Fragment? = fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER)
    if (source != null)
        savedLocationIndex =
            navigationStack.indexOf(navigationStack.find { it.location == source as NavigationInfoProvider })
}

fun navigateToRoot(
    fragmentManager: FragmentManager,
    destinationRoot: NavigationInfoProvider.NavigationRoot
) {
    when (destinationRoot) {
        NavigationInfoProvider.NavigationRoot.ROOT_PRELOG -> {
            createNavigatePreLogNavigationRoot(fragmentManager)
            destroyPostLogNavigationRoot(fragmentManager)
        }
        NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG -> {
            createNavigatePostLogNavigationRoot(fragmentManager)
            destroyPreLogNavigationRoot(fragmentManager)
        }
    }.exhaustive
}

fun navigateToBranch(
    fragmentManager: FragmentManager, destinationBranch: NavigationInfoProvider.NavigationBranch
) {
    val source = fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER)
    if ((source as NavigationInfoProvider).getNavigationBranch() == destinationBranch) return
    if ((source as NavigationInfoProvider).getNavigationRoot() != destinationBranch.root) return
    val destinationNode = navigationStack.findLast {
        it.isActive && it.location.getNavigationBranch() == destinationBranch
    } ?: createBranchOwner(fragmentManager, destinationBranch)
    navigateToFragment(fragmentManager, destinationNode.location as Fragment, false)
}

/** Creates the branch owner based on the given branch */
private fun createBranchOwner(
    fragmentManager: FragmentManager,
    branch: NavigationInfoProvider.NavigationBranch
): NavigationNode {
    when (branch) {
        NavigationInfoProvider.NavigationBranch.BRANCH_WELCOME -> {
            return addDetachFragment(fragmentManager, WelcomeFragment())
        }
        NavigationInfoProvider.NavigationBranch.BRANCH_EXPLORE -> {
            return addDetachFragment(fragmentManager, ExploreFragment())
        }
        NavigationInfoProvider.NavigationBranch.BRANCH_SEARCH -> {
            return addDetachFragment(fragmentManager, SearchFragment())
        }
        NavigationInfoProvider.NavigationBranch.BRANCH_PROFILE -> {
            return addDetachFragment(fragmentManager, ProfileFragment())
        }
        NavigationInfoProvider.NavigationBranch.BRANCH_BUSINESS -> {
            return addDetachFragment(fragmentManager, BusinessFragment())
        }
    }
}

fun getCurrentBranch(fragmentManager: FragmentManager): NavigationInfoProvider.NavigationBranch {
    return (fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER) as NavigationInfoProvider).getNavigationBranch()
}

/** Function to provide common backward navigation. Tries to find best destination that
 * makes the most sense. It will search for destinations that are shares the same branch.
 * It also animates between source and destination.
 * After the transaction, based on source's caching strategy it removes/detaches the source.
 * - If the source is already the branch owner, it doesn't navigate.
 * - If destination not found, it doesn't navigate. */
fun navigateToPrevious(
    fragmentManager: FragmentManager,
) {
    // Get source from container
    val source: Fragment? = fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER)
    // If the source is already a branch owner, return.
    if ((source as NavigationInfoProvider).isNavigationBranchOwner()) return
    // Find the best destination for the backward navigation. If not found, return.
    val destinationNode = navigationStack.findLast {
        it.isActive && it.location != (source as NavigationInfoProvider) &&
                (it.location.getNavigationBranch() == (source as NavigationInfoProvider).getNavigationBranch())
    } ?: return
    val transaction = fragmentManager.beginTransaction().setCustomAnimations(
        R.anim.enter_from_left,
        R.anim.exit_to_right,
        R.anim.enter_from_left,
        R.anim.exit_to_right,
    )
    transaction.attach(destinationNode.location as Fragment)
    val shouldCache = (source as NavigationInfoProvider).cacheOnBackPressed()
    if (shouldCache) {
        transaction.detach(source).setReorderingAllowed(true).commitNow()
        destinationNode.isActive = false
    } else {
        transaction.remove(source).setReorderingAllowed(true).commitNow()
        fragmentManager.popBackStackImmediate()
        navigationStack.removeIf { it.location == source }
    }
}

/** Function to provide common forward navigation.
 * If destination object is null. Function creates the destination.
 * @param navigationTag defines the destination.
 * @param animate determines whether the transition applies an animation.*/
fun navigateToTag(
    fragmentManager: FragmentManager,
    navigationTag: NavigationInfoProvider.NavigationTag,
    animate: Boolean
) {
    when (navigationTag) {
        NavigationInfoProvider.NavigationTag.TAG_WELCOME_FRAGMENT -> {
            var destinationNode =
                navigationStack.find { it.location.getNavigationTag() == navigationTag }
            if (destinationNode == null)
                destinationNode = addDetachFragment(fragmentManager, WelcomeFragment())
            navigateToFragment(fragmentManager, destinationNode.location as Fragment, animate)
        }
        NavigationInfoProvider.NavigationTag.TAG_SIGN_UP_FRAGMENT -> {
            var destinationNode =
                navigationStack.find { it.location.getNavigationTag() == navigationTag }
            if (destinationNode == null)
                destinationNode = addDetachFragment(fragmentManager, SignUpFragment())
            navigateToFragment(fragmentManager, destinationNode.location as Fragment, animate)
        }
        NavigationInfoProvider.NavigationTag.TAG_EXPLORE_FRAGMENT -> {
            var destinationNode =
                navigationStack.find { it.location.getNavigationTag() == navigationTag }
            if (destinationNode == null)
                destinationNode = addDetachFragment(fragmentManager, ExploreFragment())
            navigateToFragment(fragmentManager, destinationNode.location as Fragment, animate)
        }
        NavigationInfoProvider.NavigationTag.TAG_SEARCH_FRAGMENT -> {
            var destinationNode =
                navigationStack.find { it.location.getNavigationTag() == navigationTag }
            if (destinationNode == null)
                destinationNode = addDetachFragment(fragmentManager, SearchFragment())
            navigateToFragment(fragmentManager, destinationNode.location as Fragment, animate)
        }
        NavigationInfoProvider.NavigationTag.TAG_PROFILE_FRAGMENT -> {
            var destinationNode =
                navigationStack.find { it.location.getNavigationTag() == navigationTag }
            if (destinationNode == null)
                destinationNode = addDetachFragment(fragmentManager, ProfileFragment())
            navigateToFragment(fragmentManager, destinationNode.location as Fragment, animate)
        }
        NavigationInfoProvider.NavigationTag.TAG_BUSINESS_FRAGMENT -> {
            var destinationNode =
                navigationStack.find { it.location.getNavigationTag() == navigationTag }
            if (destinationNode == null)
                destinationNode = addDetachFragment(fragmentManager, BusinessFragment())
            navigateToFragment(fragmentManager, destinationNode.location as Fragment, animate)
        }
    }.exhaustive
}

/** Navigates to the given destination object.
 * @param animate determines whether the transition applies an animation.
 * */
private fun navigateToFragment(
    fragmentManager: FragmentManager,
    destination: Fragment,
    animate: Boolean
) {
    val source = fragmentManager.findFragmentById(BASE_FRAGMENT_CONTAINER)
    val transaction = fragmentManager.beginTransaction()
    if (animate) {
        transaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_right,
            R.anim.exit_to_left
        )
    }
    if (source != null) transaction.detach(source)
    transaction.attach(destination).setReorderingAllowed(true).commitNow()
    navigationStack.add(
        NavigationNode(
            destination as NavigationInfoProvider,
            isActive = true
        )
    )
}

/** Adds & Detaches all PRELOG root navigation components. */
private fun createNavigatePreLogNavigationRoot(fragmentManager: FragmentManager) {
    val destinationNode = addDetachFragment(fragmentManager, WelcomeFragment())
    navigateToFragment(fragmentManager, destinationNode.location as Fragment, true)
}

/** Removes all PRELOG root navigation components. */
private fun destroyPreLogNavigationRoot(fragmentManager: FragmentManager) {
    for (fragment in fragmentManager.fragments)
        if ((fragment as NavigationInfoProvider).getNavigationRoot() == NavigationInfoProvider.NavigationRoot.ROOT_PRELOG)
            fragmentManager.beginTransaction().remove(fragment).commitNow()
    navigationStack.removeIf {
        it.location.getNavigationRoot() == NavigationInfoProvider.NavigationRoot.ROOT_PRELOG
    }
}

/** Adds & Detaches all POSTLOG root navigation components. */
private fun createNavigatePostLogNavigationRoot(fragmentManager: FragmentManager) {
    val destinationNode = addDetachFragment(fragmentManager, ExploreFragment())
    addDetachFragment(fragmentManager, SearchFragment())
    addDetachFragment(fragmentManager, ProfileFragment())
    addDetachFragment(fragmentManager, BusinessFragment())
    navigateToFragment(fragmentManager, destinationNode.location as Fragment, true)
}

/** Removes all POSTLOG root navigation components */
private fun destroyPostLogNavigationRoot(fragmentManager: FragmentManager) {
    for (fragment in fragmentManager.fragments)
        if ((fragment as NavigationInfoProvider).getNavigationRoot() == NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG)
            fragmentManager.beginTransaction().remove(fragment).commitNow()
    navigationStack.removeIf {
        it.location.getNavigationRoot() == NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    }
}

/** Adds & Hides given destination object. */
private fun addDetachFragment(
    fragmentManager: FragmentManager,
    destination: Fragment
): NavigationNode {
    fragmentManager.beginTransaction().add(
        BASE_FRAGMENT_CONTAINER,
        destination,
        (destination as NavigationInfoProvider).getNavigationTag().name
    ).detach(destination).commitNow()
    return NavigationNode(
        destination as NavigationInfoProvider,
        isActive = true
    )
}