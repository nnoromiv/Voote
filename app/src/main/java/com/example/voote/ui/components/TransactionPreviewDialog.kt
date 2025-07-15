package com.example.voote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.firebase.data.VotePreview
import org.web3j.utils.Convert
import java.math.BigInteger

@Composable
fun TransactionPreviewDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    fromAddress: String,
    contractAddress: String,
    gasData: VotePreview,
) {
    val (gasPrice, estimateGas, nonce) = gasData

    val totalGas = gasPrice?.multiply(BigInteger.valueOf(estimateGas!!.toLong()))

    val estimatedEthFee =  Convert.fromWei(totalGas.toString(), Convert.Unit.ETHER).toPlainString()
    val gasPriceGwei = Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI).toPlainString()

    if (showDialog) {

        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),

            onDismissRequest = { onDismiss() },

            title = {
                Text(
                    "Transaction Preview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20,
                )
            },

            text = {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = "From Address",
                                modifier = Modifier.padding(end = 4.dp).size(13.dp)
                            )

                            Text(
                                "From",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13,
                                softWrap = true
                            )
                        }

                        Text(
                            fromAddress.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13,
                            softWrap = true
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.ArrowCircleDown,
                            contentDescription = "",
                            modifier = Modifier.padding(end = 4.dp).size(13.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Book,
                                contentDescription = "From Address",
                                modifier = Modifier.padding(end = 4.dp).size(13.dp)
                            )

                            Text(
                                "To",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13,
                                softWrap = true
                            )
                        }

                        Text(
                            contractAddress.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13,
                            softWrap = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(
                                    "Gas Price",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13,
                                )

                                Text(
                                    "$gasPriceGwei Gwei",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(
                                    "Gas Limit",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13,
                                )

                                Text(
                                    estimateGas.toString(),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13,
                                )
                            }
                        }

                        Box(
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(
                                    "Nonce",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13,
                                )

                                Text(
                                    nonce.toString(),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13,
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.padding(top = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Text(
                                "Estimated Fee",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13,
                            )

                            Text(
                                "$estimatedEthFee Eth",
                                fontWeight = FontWeight.Normal,
                                fontSize = 13,
                            )
                        }
                    }
                }
            },

            confirmButton = {
                PrimaryButton(
                    text = "Confirm",
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00BA11),
                        contentColor = Color.White
                    )
                )
            },

            dismissButton = {
                PrimaryButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    )
                )
            }
        )
    }
}