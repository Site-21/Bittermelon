package com.site21.bittermelon.init.neoforge;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.client.render.screenshake.StartScreenshake;
import com.site21.bittermelon.common.content.blocks.electronics.containmentpanel.networking.ContainmentNameUpdate;
import com.site21.bittermelon.common.content.blocks.electronics.containmentpanel.networking.OpenContainmentPanelScreen;
import com.site21.bittermelon.common.content.blocks.electronics.intercom.networking.*;
import com.site21.bittermelon.common.content.blocks.electronics.largeslidingdoor.networking.PlaySlidingDoorStuckSound;
import com.site21.bittermelon.common.content.blocks.electronics.largeslidingdoor.networking.UpdateSlidingDoorProgress;
import com.site21.bittermelon.common.content.blocks.electronics.largeslidingdoor.networking.UpdateSlidingDoorState;
import com.site21.bittermelon.common.content.blocks.electronics.personnelterminal.networking.OpenPersonnelScreen;
import com.site21.bittermelon.common.content.blocks.powergrid.distributionboard.networking.OpenDistributionBoardScreen;
import com.site21.bittermelon.common.content.blocks.powergrid.distributionboard.networking.ToggleBreaker;
import com.site21.bittermelon.common.content.blocks.powergrid.distributionboard.networking.ToggleMainSwitch;
import com.site21.bittermelon.common.content.blocks.stickynote.networking.OpenStickyNoteScreen;
import com.site21.bittermelon.common.content.blocks.stickynote.networking.UpdateStickyNote;
import com.site21.bittermelon.common.content.blocks.wallwriting.networking.OpenWallWritingScreen;
import com.site21.bittermelon.common.content.blocks.wallwriting.networking.UpdateWallWriting;
import com.site21.bittermelon.common.content.entities.scp650.networking.SetEntityPos;
import com.site21.bittermelon.common.content.items.scps.scp377.networking.OpenSCP3771Screen;
import com.site21.bittermelon.common.content.items.substance.networking.ReleasePressureUpdate;
import com.site21.bittermelon.common.content.items.substance.networking.TransferRateUpdate;
import com.site21.bittermelon.common.content.items.wire.networking.*;
import com.site21.bittermelon.common.content.items.wirecutters.networking.CutWire;
import com.site21.bittermelon.common.content.items.wirecutters.networking.OpenWireCutterScreen;
import com.site21.bittermelon.common.content.items.writablepaper.networking.OpenPaperEditScreen;
import com.site21.bittermelon.common.content.mobeffects.electrocuted.networking.CutOffChat;
import com.site21.bittermelon.common.systems.atmosphere.networking.*;
import com.site21.bittermelon.common.systems.carry.ThrowCarriedEntity;
import com.site21.bittermelon.common.systems.character.networking.*;
import com.site21.bittermelon.common.systems.economy.bank.networking.OpenATMScreen;
import com.site21.bittermelon.common.systems.electronics.privilege.networking.OpenPrivilegeEditorScreen;
import com.site21.bittermelon.common.systems.electronics.privilege.networking.RemovePrivilegeForBE;
import com.site21.bittermelon.common.systems.electronics.privilege.networking.SetPrivilegeForBE;
import com.site21.bittermelon.common.systems.fluid.substance.UpdateBlockAt;
import com.site21.bittermelon.common.systems.medical.legacy.networking.*;
import com.site21.bittermelon.common.systems.medical.wound.networking.WoundPacket;
import com.site21.bittermelon.common.systems.personnel.privilege.networking.*;
import com.site21.bittermelon.common.systems.personnel.registry.networking.AddPersonnelEntry;
import com.site21.bittermelon.common.systems.personnel.registry.networking.RemovePersonnelEntry;
import com.site21.bittermelon.common.systems.personnel.registry.networking.SyncPersonnelRegistry;
import com.site21.bittermelon.common.systems.personnel.registry.networking.UpdatePersonnelEntry;
import com.site21.bittermelon.common.systems.ragdoll.networking.RagdollPlayer;
import com.site21.bittermelon.common.systems.roles.networking.AddRole;
import com.site21.bittermelon.common.systems.stumble.networking.AttemptToRise;
import com.site21.bittermelon.common.systems.telecomms.intercom.networking.AddIntercomToClient;
import com.site21.bittermelon.common.systems.telecomms.intercom.networking.RemoveIntercomFromClient;
import com.site21.bittermelon.common.systems.telecomms.intercom.networking.SyncIntercomList;
import com.site21.bittermelon.common.systems.throwing.ThrowItemPacket;
import com.site21.bittermelon.networking.client.ClearForcedPose;
import com.site21.bittermelon.networking.client.ContainerDataUpdate;
import com.site21.bittermelon.networking.client.OpenCPRScreen;
import com.site21.bittermelon.networking.client.SetForcedPose;
import com.site21.bittermelon.networking.server.AddEffect;
import com.site21.bittermelon.networking.server.SetLastTypingTime;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Bittermelon.MOD_ID)
public class PayloadRegistration {
    @SubscribeEvent
    public static void register(final @NotNull RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0");

        registrar.playToServer(
                TransferRateUpdate.TYPE,
                TransferRateUpdate.STREAM_CODEC,
                TransferRateUpdate::handle
        );

        registrar.playToServer(
                ContainerDataUpdate.TYPE,
                ContainerDataUpdate.STREAM_CODEC,
                ContainerDataUpdate::handle
        );

        registrar.playToClient(
                SetForcedPose.TYPE,
                SetForcedPose.STREAM_CODEC,
                SetForcedPose::handle
        );

        registrar.playToClient(
                ClearForcedPose.TYPE,
                ClearForcedPose.STREAM_CODEC,
                ClearForcedPose::handle
        );

        registrar.playToClient(
                OpenCPRScreen.TYPE,
                OpenCPRScreen.STREAM_CODEC,
                OpenCPRScreen::handle
        );

        registrar.playToServer(
                ThrowItemPacket.TYPE,
                ThrowItemPacket.STREAM_CODEC,
                ThrowItemPacket::handle
        );

        registrar.playToClient(
                OpenATMScreen.TYPE,
                OpenATMScreen.STREAM_CODEC,
                OpenATMScreen::handle
        );

        registrar.playToClient(
                OpenContainmentPanelScreen.TYPE,
                OpenContainmentPanelScreen.STREAM_CODEC,
                OpenContainmentPanelScreen::handle
        );

        registrar.playToServer(
                ContainmentNameUpdate.TYPE,
                ContainmentNameUpdate.STREAM_CODEC,
                ContainmentNameUpdate::handle
        );

        registrar.playToClient(
                AtmosChunkUpdate.TYPE,
                AtmosChunkUpdate.STREAM_CODEC,
                AtmosChunkUpdate::handle
        );

        registrar.playToServer(
                ReleasePressureUpdate.TYPE,
                ReleasePressureUpdate.STREAM_CODEC,
                ReleasePressureUpdate::handle
        );

        registrar.playToServer(
                IntercomIDUpdate.TYPE,
                IntercomIDUpdate.STREAM_CODEC,
                IntercomIDUpdate::handle
        );

        registrar.playToServer(
                IntercomMicUpdate.TYPE,
                IntercomMicUpdate.STREAM_CODEC,
                IntercomMicUpdate::handle
        );

        registrar.playToServer(
                IntercomSpeakerUpdate.TYPE,
                IntercomSpeakerUpdate.STREAM_CODEC,
                IntercomSpeakerUpdate::handle
        );

        registrar.playToServer(
                IntercomTargetUpdate.TYPE,
                IntercomTargetUpdate.STREAM_CODEC,
                IntercomTargetUpdate::handle
        );

        registrar.playToClient(
                SyncIntercomList.TYPE,
                SyncIntercomList.STREAM_CODEC,
                SyncIntercomList::handle
        );

        registrar.playToClient(
                SyncAtmosInstances.TYPE,
                SyncAtmosInstances.STREAM_CODEC,
                SyncAtmosInstances::handle
        );

        registrar.playToClient(
                SyncAtmosInstance.TYPE,
                SyncAtmosInstance.STREAM_CODEC,
                SyncAtmosInstance::handle
        );

        registrar.playToClient(
                SyncCharacters.TYPE,
                SyncCharacters.STREAM_CODEC,
                SyncCharacters::handle
        );

        registrar.playToClient(
                CreateAtmosInstance.TYPE,
                CreateAtmosInstance.STREAM_CODEC,
                CreateAtmosInstance::handle
        );

        registrar.playToClient(
                RemoveAtmosInstance.TYPE,
                RemoveAtmosInstance.STREAM_CODEC,
                RemoveAtmosInstance::handle
        );

        registrar.playToClient(
                UpdateAtmosTemperature.TYPE,
                UpdateAtmosTemperature.STREAM_CODEC,
                UpdateAtmosTemperature::handle
        );

        registrar.playToClient(
                UpdateAtmosBlocks.TYPE,
                UpdateAtmosBlocks.STREAM_CODEC,
                UpdateAtmosBlocks::handle
        );

        registrar.playToClient(
                UpdateAtmosGas.TYPE,
                UpdateAtmosGas.STREAM_CODEC,
                UpdateAtmosGas::handle
        );

        registrar.playToServer(
                WiringDataUpdate.TYPE,
                WiringDataUpdate.STREAM_CODEC,
                WiringDataUpdate::handle
        );

        registrar.playToServer(
                MakeWireConnection.TYPE,
                MakeWireConnection.STREAM_CODEC,
                MakeWireConnection::handle
        );

        registrar.playToClient(
                RefreshHealthScreen.TYPE,
                RefreshHealthScreen.STREAM_CODEC,
                RefreshHealthScreen::handle
        );

        registrar.playToClient(
                OpenIntercomScreen.TYPE,
                OpenIntercomScreen.STREAM_CODEC,
                OpenIntercomScreen::handle
        );

        registrar.playToClient(
                OpenWiringScreen.TYPE,
                OpenWiringScreen.STREAM_CODEC,
                OpenWiringScreen::handle
        );

        registrar.playToServer(
                AttemptToRise.TYPE,
                AttemptToRise.STREAM_CODEC,
                AttemptToRise::handle
        );

        registrar.playToClient(
                StartScreenshake.TYPE,
                StartScreenshake.STREAM_CODEC,
                StartScreenshake::handle
        );

        registrar.playToServer(
                RemoveWiringData.TYPE,
                RemoveWiringData.STREAM_CODEC,
                RemoveWiringData::handle
        );

        registrar.playToServer(
                UpdateSlidingDoorState.TYPE,
                UpdateSlidingDoorState.STREAM_CODEC,
                UpdateSlidingDoorState::handle
        );

        registrar.playToServer(
                PlaySlidingDoorStuckSound.TYPE,
                PlaySlidingDoorStuckSound.STREAM_CODEC,
                PlaySlidingDoorStuckSound::handle
        );

        registrar.playToServer(
                UpdateSlidingDoorProgress.TYPE,
                UpdateSlidingDoorProgress.STREAM_CODEC,
                UpdateSlidingDoorProgress::handle
        );

        registrar.playToClient(
                SetEntityPos.TYPE,
                SetEntityPos.STREAM_CODEC,
                SetEntityPos::handle
        );

        registrar.playToClient(
                OpenPaperEditScreen.TYPE,
                OpenPaperEditScreen.STREAM_CODEC,
                OpenPaperEditScreen::handle
        );

        registrar.playToClient(
                CutOffChat.TYPE,
                CutOffChat.STREAM_CODEC,
                CutOffChat::handle
        );

        registrar.playToClient(
                OpenPersonnelScreen.TYPE,
                OpenPersonnelScreen.STREAM_CODEC,
                OpenPersonnelScreen::handle
        );

        registrar.playToClient(
                AddIntercomToClient.TYPE,
                AddIntercomToClient.STREAM_CODEC,
                AddIntercomToClient::handle
        );

        registrar.playToClient(
                RemoveIntercomFromClient.TYPE,
                RemoveIntercomFromClient.STREAM_CODEC,
                RemoveIntercomFromClient::handle
        );

        registrar.commonBidirectional(
                AddPersonnelEntry.TYPE,
                AddPersonnelEntry.STREAM_CODEC,
                AddPersonnelEntry::handle
        );

        registrar.playToClient(
                SyncPersonnelRegistry.TYPE,
                SyncPersonnelRegistry.STREAM_CODEC,
                SyncPersonnelRegistry::handle
        );

        registrar.playToClient(
                SyncPrivileges.TYPE,
                SyncPrivileges.STREAM_CODEC,
                SyncPrivileges::handle
        );

        registrar.commonBidirectional(
                AddPrivilege.TYPE,
                AddPrivilege.STREAM_CODEC,
                AddPrivilege::handle
        );

        registrar.commonBidirectional(
                SetPrivilegeForGroup.TYPE,
                SetPrivilegeForGroup.STREAM_CODEC,
                SetPrivilegeForGroup::handle
        );

        registrar.commonBidirectional(
                RemovePrivilegeForGroup.TYPE,
                RemovePrivilegeForGroup.STREAM_CODEC,
                RemovePrivilegeForGroup::handle
        );

        registrar.commonBidirectional(
                SetPrivilegeForEntry.TYPE,
                SetPrivilegeForEntry.STREAM_CODEC,
                SetPrivilegeForEntry::handle
        );

        registrar.commonBidirectional(
                RemovePrivilegeForEntry.TYPE,
                RemovePrivilegeForEntry.STREAM_CODEC,
                RemovePrivilegeForEntry::handle
        );

        registrar.commonBidirectional(
                RemovePrivilege.TYPE,
                RemovePrivilege.STREAM_CODEC,
                RemovePrivilege::handle
        );

        registrar.commonBidirectional(
                AddPrivilegeGroup.TYPE,
                AddPrivilegeGroup.STREAM_CODEC,
                AddPrivilegeGroup::handle
        );

        registrar.commonBidirectional(
                RemovePrivilegeGroup.TYPE,
                RemovePrivilegeGroup.STREAM_CODEC,
                RemovePrivilegeGroup::handle
        );

        registrar.commonBidirectional(
                RemovePersonnelEntry.TYPE,
                RemovePersonnelEntry.STREAM_CODEC,
                RemovePersonnelEntry::handle
        );

        registrar.commonBidirectional(
                UpdatePersonnelEntry.TYPE,
                UpdatePersonnelEntry.STREAM_CODEC,
                UpdatePersonnelEntry::handle
        );

        registrar.playToServer(
                SwitchCharacter.TYPE,
                SwitchCharacter.STREAM_CODEC,
                SwitchCharacter::handle
        );

        registrar.playToServer(
                AddEffect.TYPE,
                AddEffect.STREAM_CODEC,
                AddEffect::handle
        );

        registrar.playBidirectional(
                UpdateCharacter.TYPE,
                UpdateCharacter.STREAM_CODEC,
                UpdateCharacter::handle
        );

        registrar.playToServer(
                OpenCharacterScreenC2S.TYPE,
                OpenCharacterScreenC2S.STREAM_CODEC,
                OpenCharacterScreenC2S::handle
        );

        registrar.playToClient(
                OpenCharacterScreenS2C.TYPE,
                OpenCharacterScreenS2C.STREAM_CODEC,
                OpenCharacterScreenS2C::handle
        );

        registrar.playToServer(
                AddRole.TYPE,
                AddRole.STREAM_CODEC,
                AddRole::handle
        );

        registrar.playToClient(
                SyncActiveCharacter.TYPE,
                SyncActiveCharacter.STREAM_CODEC,
                SyncActiveCharacter::handle
        );

        registrar.playBidirectional(
                SetLastTypingTime.TYPE,
                SetLastTypingTime.STREAM_CODEC,
                SetLastTypingTime::handle
        );

        registrar.playToClient(
                OpenSCP3771Screen.TYPE,
                OpenSCP3771Screen.STREAM_CODEC,
                OpenSCP3771Screen::handle
        );

        registrar.playToServer(
                SetPrivilegeForBE.TYPE,
                SetPrivilegeForBE.STREAM_CODEC,
                SetPrivilegeForBE::handle
        );

        registrar.playToServer(
                RemovePrivilegeForBE.TYPE,
                RemovePrivilegeForBE.STREAM_CODEC,
                RemovePrivilegeForBE::handle
        );

        registrar.playToClient(
                OpenPrivilegeEditorScreen.TYPE,
                OpenPrivilegeEditorScreen.STREAM_CODEC,
                OpenPrivilegeEditorScreen::handle
        );

        registrar.playToClient(
                OpenDistributionBoardScreen.TYPE,
                OpenDistributionBoardScreen.STREAM_CODEC,
                OpenDistributionBoardScreen::handle
        );

        registrar.playToServer(
                ToggleBreaker.TYPE,
                ToggleBreaker.STREAM_CODEC,
                ToggleBreaker::handle
        );

        registrar.playToServer(
                ToggleMainSwitch.TYPE,
                ToggleMainSwitch.STREAM_CODEC,
                ToggleMainSwitch::handle
        );

        registrar.playToServer(
                UpdateWallWriting.TYPE,
                UpdateWallWriting.STREAM_CODEC,
                UpdateWallWriting::handle
        );

        registrar.playToClient(
                OpenWallWritingScreen.TYPE,
                OpenWallWritingScreen.STREAM_CODEC,
                OpenWallWritingScreen::handle
        );

        registrar.playBidirectional(
                UpdateCompartments.TYPE,
                UpdateCompartments.STREAM_CODEC,
                UpdateCompartments::handle
        );

        registrar.playToClient(
                OpenStickyNoteScreen.TYPE,
                OpenStickyNoteScreen.STREAM_CODEC,
                OpenStickyNoteScreen::handle
        );

        registrar.playToServer(
                UpdateStickyNote.TYPE,
                UpdateStickyNote.STREAM_CODEC,
                UpdateStickyNote::handle
        );

        registrar.playToServer(
                SpliceOutputWire.TYPE,
                SpliceOutputWire.STREAM_CODEC,
                SpliceOutputWire::handle
        );

        registrar.playToServer(
                SpliceInputWire.TYPE,
                SpliceInputWire.STREAM_CODEC,
                SpliceInputWire::handle
        );

        registrar.playToServer(
                CutWire.TYPE,
                CutWire.STREAM_CODEC,
                CutWire::handle
        );

        registrar.playToClient(
                OpenWireCutterScreen.TYPE,
                OpenWireCutterScreen.STREAM_CODEC,
                OpenWireCutterScreen::handle
        );

        registrar.playBidirectional(
                InsertCompartment.TYPE,
                InsertCompartment.STREAM_CODEC,
                InsertCompartment::handle
        );

        registrar.playToServer(
                SetCharactersChanged.TYPE,
                SetCharactersChanged.STREAM_CODEC,
                SetCharactersChanged::handle
        );

        registrar.playBidirectional(
                AddAndInsertCompartment.TYPE,
                AddAndInsertCompartment.STREAM_CODEC,
                AddAndInsertCompartment::handle
        );

        registrar.playBidirectional(
                RemoveCompartment.TYPE,
                RemoveCompartment.STREAM_CODEC,
                RemoveCompartment::handle
        );

        registrar.playToServer(
                CreateCharacter.TYPE,
                CreateCharacter.STREAM_CODEC,
                CreateCharacter::handle
        );

        registrar.playToServer(
                ThrowCarriedEntity.TYPE,
                ThrowCarriedEntity.STREAM_CODEC,
                ThrowCarriedEntity::handle
        );

        registrar.playToClient(
                UpdateBlockAt.TYPE,
                UpdateBlockAt.STREAM_CODEC,
                UpdateBlockAt::handle
        );

        registrar.playToClient(
                WoundPacket.TYPE,
                WoundPacket.STREAM_CODEC,
                WoundPacket::handle
        );

        registrar.playToServer(
                RagdollPlayer.TYPE,
                RagdollPlayer.STREAM_CODEC,
                RagdollPlayer::handle
        );
    }
}
