package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.AquariumProfileEntity
import com.example.model.VolumeUnit
import com.example.ui.formatLiters
import com.example.ui.formatVolumeForUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun shareProfileSummary(context: Context, profile: AquariumProfileEntity) {
  val sendIntent =
    Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Profil Akuarium: ${profile.profileName}")
      putExtra(Intent.EXTRA_TEXT, profile.toShareSummaryText())
    }
  val chooser = Intent.createChooser(sendIntent, "Bagikan Profil Akuarium")
  runCatching { context.startActivity(chooser) }
}

@Composable
fun SavedProfilesScreen(
  profiles: List<AquariumProfileEntity>,
  activeProfileId: Long?,
  onLoadProfile: (AquariumProfileEntity) -> Unit,
  onRenameProfile: (AquariumProfileEntity, String) -> Unit,
  onDeleteProfile: (AquariumProfileEntity) -> Unit,
  onGoToCalculator: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  var selectedDetailProfile by remember { mutableStateOf<AquariumProfileEntity?>(null) }
  var profileToRename by remember { mutableStateOf<AquariumProfileEntity?>(null) }
  var profileToDelete by remember { mutableStateOf<AquariumProfileEntity?>(null) }

  if (profiles.isEmpty()) {
    Column(
      modifier = modifier.fillMaxSize().padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
        modifier = Modifier.size(80.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.BookmarkBorder,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(20.dp),
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Belum Ada Profil Tersimpan",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text =
          "Hitung volume air akuarium Anda lalu ketuk \"Simpan Profil\" untuk menyimpannya secara lokal.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(20.dp))
      Button(
        onClick = onGoToCalculator,
        modifier = Modifier.testTag("empty_go_calculator_button"),
      ) {
        Text("Buka Kalkulator")
      }
    }
  } else {
    LazyColumn(
      modifier = modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(profiles, key = { it.id }) { profile ->
        SavedProfileCard(
          profile = profile,
          isActive = profile.id == activeProfileId,
          onOpenDetail = { selectedDetailProfile = profile },
          onLoad = { onLoadProfile(profile) },
          onShareClick = { shareProfileSummary(context, profile) },
          onRenameClick = { profileToRename = profile },
          onDeleteClick = { profileToDelete = profile },
        )
      }
    }
  }

  selectedDetailProfile?.let { detailProfile ->
    ProfileDetailDialog(
      profile = detailProfile,
      onDismiss = { selectedDetailProfile = null },
      onShare = { shareProfileSummary(context, detailProfile) },
      onLoad = {
        selectedDetailProfile = null
        onLoadProfile(detailProfile)
      },
    )
  }

  profileToRename?.let { target ->
    ProfileNameDialog(
      title = "Ubah Nama Profil",
      confirmLabel = "Simpan Nama",
      initialName = target.profileName,
      onDismiss = { profileToRename = null },
      onConfirm = { newName ->
        onRenameProfile(target, newName)
        profileToRename = null
      },
    )
  }

  profileToDelete?.let { target ->
    AlertDialog(
      onDismissRequest = { profileToDelete = null },
      title = { Text("Hapus Profil?") },
      text = {
        Text("Profil \"${target.profileName}\" akan dihapus dari penyimpanan lokal perangkat.")
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteProfile(target)
            profileToDelete = null
          },
          modifier = Modifier.testTag("confirm_delete_profile_button"),
        ) {
          Text("Hapus")
        }
      },
      dismissButton = {
        TextButton(onClick = { profileToDelete = null }) { Text("Batal") }
      },
    )
  }
}

@Composable
private fun SavedProfileCard(
  profile: AquariumProfileEntity,
  isActive: Boolean,
  onOpenDetail: () -> Unit,
  onLoad: () -> Unit,
  onShareClick: () -> Unit,
  onRenameClick: () -> Unit,
  onDeleteClick: () -> Unit,
) {
  val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }
  val formattedDate = remember(profile.updatedAtMillis) {
    dateFormatter.format(Date(profile.updatedAtMillis))
  }

  ElevatedCard(
    modifier =
      Modifier.fillMaxWidth()
        .clip(MaterialTheme.shapes.large)
        .clickable(onClick = onOpenDetail)
        .testTag("saved_profile_card_${profile.id}"),
    shape = MaterialTheme.shapes.large,
    colors =
      CardDefaults.elevatedCardColors(
        containerColor =
          if (isActive) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
          } else {
            MaterialTheme.colorScheme.surface
          }
      ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
              text = profile.profileName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false),
            )
            if (isActive) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primary,
              ) {
                Text(
                  text = "Aktif",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
              }
            }
          }
          Text(
            text = "${profile.lengthCm} × ${profile.widthCm} × ${profile.heightCm} cm  •  $formattedDate",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        ) {
          Text(
            text = profile.condition.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          )
        }
      }

      // Volume Metrics Strip
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.WaterDrop,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp),
            )
            Text(
              text = "${formatLiters(profile.netWaterVolumeLiters)} Liter",
              style =
                MaterialTheme.typography.titleMedium.copy(
                  fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
                  fontWeight = FontWeight.Bold,
                ),
              color = MaterialTheme.colorScheme.primary,
            )
          }

          Text(
            text =
              if (profile.totalDecorationDisplacedLiters > 0.0) {
                "Polos ${formatLiters(profile.bareWaterVolumeLiters, 1)} L (-${formatLiters(profile.totalDecorationDisplacedLiters, 1)} L)"
              } else {
                "Polos ${formatLiters(profile.bareWaterVolumeLiters, 1)} L"
              },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // Card Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilledTonalButton(
            onClick = onLoad,
            modifier = Modifier.testTag("load_profile_button_${profile.id}"),
          ) {
            Icon(
              imageVector = Icons.Rounded.OpenInBrowser,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text("Muat")
          }

          OutlinedButton(
            onClick = onShareClick,
            modifier = Modifier.testTag("share_profile_button_${profile.id}"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Share,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text("Bagikan")
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
          IconButton(
            onClick = onRenameClick,
            modifier = Modifier.size(48.dp).testTag("rename_profile_button_${profile.id}"),
          ) {
            Icon(
              imageVector = Icons.Rounded.DriveFileRenameOutline,
              contentDescription = "Ubah nama ${profile.profileName}",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(48.dp).testTag("delete_profile_button_${profile.id}"),
          ) {
            Icon(
              imageVector = Icons.Rounded.DeleteOutline,
              contentDescription = "Hapus ${profile.profileName}",
              tint = MaterialTheme.colorScheme.error,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProfileDetailDialog(
  profile: AquariumProfileEntity,
  onDismiss: () -> Unit,
  onShare: () -> Unit,
  onLoad: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = profile.profileName, style = MaterialTheme.typography.titleLarge)
        Text(
          text = "Kondisi: ${profile.condition.label}",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailLine("Dimensi Kaca", "${profile.lengthCm} × ${profile.widthCm} × ${profile.heightCm} cm")
        DetailLine("Ruang Kosong Atas", "${profile.topGapCm} cm")
        if (profile.glassThicknessMm > 0) {
          DetailLine("Tebal Kaca", "${profile.glassThicknessMm} mm")
        }
        if (profile.condition.includesSand) {
          DetailLine(
            "Pasir (P×L×T)",
            "${profile.sandLengthCm} × ${profile.sandWidthCm} × ${profile.sandHeightCm} cm",
          )
        }
        if (profile.condition.includesStone) {
          DetailLine(
            "Batu",
            "${profile.stoneCount} buah (${profile.stoneWeightGramsPerUnit} g/buah)",
          )
        }
        if (profile.condition.includesWood) {
          DetailLine(
            "Kayu",
            "${profile.woodCount} potong (${profile.woodWeightGramsPerUnit} g, ${profile.woodSubmergedPercent}%)",
          )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        DetailLine(
          "Volume Air (Liter)",
          "${formatVolumeForUnit(profile.netWaterVolumeLiters, VolumeUnit.LITER)} L",
          highlight = true,
        )
        DetailLine(
          "Galon US",
          "${formatVolumeForUnit(profile.netWaterVolumeLiters, VolumeUnit.US_GALLON)} gal US",
        )
        DetailLine(
          "Galon Imperial",
          "${formatVolumeForUnit(profile.netWaterVolumeLiters, VolumeUnit.IMPERIAL_GALLON)} gal UK",
        )
        DetailLine(
          "Sentimeter Kubik",
          "${formatVolumeForUnit(profile.netWaterVolumeLiters, VolumeUnit.CUBIC_CM)} cm³",
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onShare,
        modifier = Modifier.testTag("detail_share_profile_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.Share,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text("Bagikan")
      }
    },
    dismissButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        TextButton(onClick = onDismiss) { Text("Tutup") }
        TextButton(
          onClick = onLoad,
          modifier = Modifier.testTag("detail_load_profile_button"),
        ) {
          Text("Muat")
        }
      }
    },
  )
}

@Composable
private fun DetailLine(
  label: String,
  value: String,
  highlight: Boolean = false,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = value,
      style =
        MaterialTheme.typography.labelLarge.copy(
          fontFamily = MaterialTheme.typography.displaySmall.fontFamily
        ),
      color =
        if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
    )
  }
}

@Composable
fun ProfileNameDialog(
  title: String,
  confirmLabel: String,
  initialName: String,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit,
) {
  var nameText by remember(initialName) { mutableStateOf(initialName) }
  val isValid = nameText.trim().isNotEmpty()

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Beri nama profil agar mudah dikenali (mis. Aquascape Ruang Tamu).",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
          value = nameText,
          onValueChange = { if (it.length <= 40) nameText = it },
          singleLine = true,
          label = { Text("Nama Profil Akuarium") },
          placeholder = { Text("Contoh: Aquascape 60P") },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { if (isValid) onConfirm(nameText.trim()) },
        enabled = isValid,
        modifier = Modifier.testTag("confirm_save_profile_button"),
      ) {
        Text(confirmLabel)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Batal") }
    },
  )
}
