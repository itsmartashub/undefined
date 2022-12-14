package app.revanced.patches.youtube.layout.castbutton.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.castbutton.annotations.CastButtonCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("hide-cast-button")
@Description("Hides the cast button in the video player.")
@CastButtonCompatibility
@Version("0.0.1")
class HideCastButtonPatch : BytecodePatch() {
    override fun execute(context: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_cast_button_enabled",
                StringResource("revanced_cast_button_enabled_title", "Show cast button"),
                false,
                StringResource("revanced_cast_button_summary_on", "Cast button is shown"),
                StringResource("revanced_cast_button_summary_off", "Cast button is hidden")
            )
        )

        with(
            context.findClass("MediaRouteButton")
                ?: return PatchResultError("MediaRouteButton class not found.")
        ) {
            with(
                mutableClass.methods.find { it.name == "setVisibility" }
                    ?: return PatchResultError("setVisibility method not found.")
            ) {
                addInstructions(
                    0, """
                            invoke-static {p1}, Lapp/revanced/integrations/patches/HideCastButtonPatch;->getCastButtonOverrideV2(I)I
                            move-result p1
                        """
                )
            }
        }

        return PatchResultSuccess()
    }
}
